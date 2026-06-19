package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SegmentRevenueVO {

    private Long id;
    private String orderNo;
    private Long machineId;
    private String machineCode;
    private Long mallId;
    private String mallName;
    private BigDecimal commissionRate;
    private BigDecimal totalAmount;
    private BigDecimal mallCommission;
    private BigDecimal supplierShare;
    private BigDecimal operatorShare;
    private BigDecimal platformShare;
    private String settleStatus;
    private LocalDateTime orderTime;
    private String periodType;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
