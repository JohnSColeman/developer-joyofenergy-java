package uk.tw.energy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public MeterReadingController(MeterReadingService meterReadingService, PricePlanService pricePlanService, AccountService accountService) {
        this.meterReadingService = meterReadingService;
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @PostMapping("/store")
    public ResponseEntity<Void> storeReadings(@RequestBody MeterReadings meterReadings) {
        if (meterReadings.isNotValid()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usage/{smartMeterId}")
    public ResponseEntity<UsageCost> usageCost(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<PricePlan> plan = pricePlanService.getPricePlan(pricePlanId);
        if (plan.isEmpty()) return ResponseEntity.notFound().build();
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        if (readings.isEmpty())  return ResponseEntity.notFound().build();
        BigDecimal cost = pricePlanService.calculateUsageCost(readings.get(), plan.get());
        return ResponseEntity.ok(new UsageCost(cost));
    }
}
