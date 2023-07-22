package uk.tw.energy.domain;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.math.BigDecimal;

public class UsageCostFactory implements Validations {

    public static final UsageCostFactory INSTANCE = new UsageCostFactory();

    private UsageCostFactory() {
    }

    public Validation<Seq<String>, UsageCost> of(BigDecimal cost) {
        return Validation.combine(
                validateNotNull(cost, "usage cost cannot be null"),
                validateNotZero(cost, "usage cost cannot be 0")
        ).ap((cost1, cost2) -> new UsageCost(cost));
    }
}
