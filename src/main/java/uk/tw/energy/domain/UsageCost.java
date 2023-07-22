package uk.tw.energy.domain;

import java.math.BigDecimal;

public class UsageCost {
    private BigDecimal cost;

    public UsageCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }
}