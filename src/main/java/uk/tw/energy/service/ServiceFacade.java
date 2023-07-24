package uk.tw.energy.service;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.*;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceFacade implements Validations {
    private final MeterReadingService meterReadingService;
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public ServiceFacade(MeterReadingService meterReadingService, PricePlanService pricePlanService, AccountService accountService) {
        this.meterReadingService = meterReadingService;
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    public Validation<Seq<String>, UsageCost> calculateUsageCost(String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<PricePlan> plan = pricePlanService.getPricePlan(pricePlanId);
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return Validation.combine(
                        validatePresent(plan, "price plan not found"),
                        validatePresent(readings, "electricity readings not found")
                ).ap((validPlan, validReadings) -> pricePlanService.calculateUsageCost(validReadings, validPlan))
                .fold(
                        Validation::invalid,
                        UsageCostFactory.INSTANCE::of
                );
    }
}
