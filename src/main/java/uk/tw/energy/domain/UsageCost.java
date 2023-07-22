package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record UsageCost(BigDecimal cost) {
    public UsageCost(BigDecimal cost) {
        this.cost = cost.setScale(2, RoundingMode.HALF_UP);
    }
}