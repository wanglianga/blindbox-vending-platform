package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesStatsVO {

    private Long orderCount;
    private BigDecimal totalAmount;
    private BigDecimal mallCommission;
    private Long machineCount;
}
