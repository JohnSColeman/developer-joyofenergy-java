package uk.tw.energy.controller;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.ServiceFacade;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readings")
public class MeterReadingController implements ResponseEntityFactory {

    private final MeterReadingService meterReadingService;
    private final ServiceFacade serviceFacade;


    public MeterReadingController(MeterReadingService meterReadingService,ServiceFacade serviceFacade) {
        this.meterReadingService = meterReadingService;
        this.serviceFacade = serviceFacade;
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
    public ResponseEntity<Validation<Seq<String>, UsageCost>> usageCost(@PathVariable String smartMeterId) {
        return responseEntity(serviceFacade.calculateUsageCost(smartMeterId));
    }
}
