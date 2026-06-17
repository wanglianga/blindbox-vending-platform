package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueStatsVO {

    private BigDecimal totalAmount;
    private BigDecimal mallCommission;
    private BigDecimal supplierShare;
    private BigDecimal operatorShare;
    private BigDecimal platformShare;
    private Long orderCount;
}
