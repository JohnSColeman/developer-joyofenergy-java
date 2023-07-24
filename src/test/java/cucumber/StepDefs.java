package cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.SeedingApplicationDataConfiguration;
import uk.tw.energy.controller.MeterReadingController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;
import uk.tw.energy.service.ServiceFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StepDefs {

    private final MeterReadingService meterReadingService = new MeterReadingService(new HashMap<>());
    private final List<PricePlan> pricePlans = new ArrayList<>();
    private String smartMeterId;
    private ZoneId zoneId;
    private List<ElectricityReading> electricityReadings;
    private Map<String, BigDecimal> consumptionCosts;

    private SeedingApplicationDataConfiguration beanConfigs = new SeedingApplicationDataConfiguration();
    private MeterReadingController meterReadingController;

    public StepDefs() {
        ServiceFacade serviceFacade = new ServiceFacade(meterReadingService,
                new PricePlanService(beanConfigs.pricePlans(), meterReadingService),
                new AccountService(beanConfigs.smartMeterToPricePlanAccounts()));
        this.meterReadingController = new MeterReadingController(meterReadingService, serviceFacade);
    }

    @Given("a smart meter with ID {string}")
    public void aSmartMeterWithID(String smartMeterId) {
        this.smartMeterId = smartMeterId;
    }

    @Given("a smart meter with ID {string} and time zone {string}")
    public void a_smart_meter_with_id_and_time_zone_offset(String smartMeterId, String timeZone) {
        this.smartMeterId = smartMeterId;
        this.zoneId = ZoneId.of(timeZone);
    }

    @Given("the following electricity readings for the smart meter:")
    public void theFollowingElectricityReadingsForTheSmartMeter(List<Map<String, String>> readingsTable) {
        electricityReadings = readingsTable.stream()
                .map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row.get("Date"), DateTimeFormatter.ofPattern("EEEE d MMM yyyy HH:mm"));
                    BigDecimal reading = new BigDecimal(row.get("Reading"));
                    return new ElectricityReading(time.atZone(zoneId).toInstant(), reading);
                })
                .collect(Collectors.toList());

        meterReadingService.storeReadings(smartMeterId, electricityReadings);
    }

    @Given("the following price plans:")
    public void theFollowingPricePlans(List<Map<String, String>> pricePlansTable) {
        pricePlansTable.forEach(row -> {
            String planName = row.get("Plan Name");
            String energySupplier = row.get("Energy Supplier");
            BigDecimal unitRate = new BigDecimal(row.get("Unit Rate"));
            List<PricePlan.PeakTimeMultiplier> peakTimeMultipliers = new ArrayList<>();

            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                String dayName = dayOfWeek.name();
                String capitalisedDayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();
                BigDecimal multiplier = new BigDecimal(row.get(capitalisedDayName + " Multiplier"));
                peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(dayOfWeek, multiplier));
            }

            pricePlans.add(new PricePlan(planName, energySupplier, unitRate, peakTimeMultipliers));
        });
    }

    @When("I calculate the consumption cost for each price plan")
    public void iCalculateTheConsumptionCostForEachPricePlan() {
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);
        consumptionCosts = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId)
                .orElseThrow(() -> new RuntimeException("Failed to calculate consumption costs."));
    }

    @Then("the consumption cost for each price plan should be:")
    public void theConsumptionCostForEachPricePlanShouldBe(List<Map<String, String>> costsTable) {
        costsTable.forEach(row -> {
            String energySupplier = row.get("Energy Supplier");
            String pricePlan = row.get("Price Plan");
            BigDecimal expectedCost = new BigDecimal(row.get("Cost"));
            BigDecimal actualCost = consumptionCosts.get(pricePlan);
            Assert.assertEquals(expectedCost, actualCost);
        });
    }

    @Given("the following readings for the smart meter:")
    public void the_following_readings_for_the_smart_meter(List<Map<String, String>> readingsTable) {
        electricityReadings = readingsTable.stream()
                .map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row.get("Date"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"));
                    BigDecimal reading = new BigDecimal(row.get("Reading"));
                    return new ElectricityReading(time.atZone(zoneId).toInstant(), reading);
                })
                .collect(Collectors.toList());
        meterReadingService.storeReadings(smartMeterId, electricityReadings);
    }

    @Then("the weekly usage cost is {double}")
    public void the_weekly_usage_cost_is(Double cost) {
        ResponseEntity<Validation<Seq<String>, UsageCost>> response = meterReadingController.usageCost(smartMeterId);
        Assert.assertEquals(BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP), response.getBody().get().cost());
    }

    @Then("the weekly usage cost is not found")
    public void theWeeklyUsageCostIsNotFound() {
        ResponseEntity<Validation<Seq<String>, UsageCost>> response = meterReadingController.usageCost(smartMeterId);
        Assert.assertTrue("response is 4xx", response.getStatusCode().is4xxClientError());
    }
}
