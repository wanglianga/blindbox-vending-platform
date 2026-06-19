package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryCorrectionVO {

    private Long id;
    private String correctionNo;
    private Long stuckRecordId;
    private String stuckRecordNo;
    private Long machineId;
    private String machineCode;
    private String machineName;
    private Integer gridNo;
    private Long skuId;
    private String skuName;
    private String skuImage;
    private Integer beforeInventory;
    private Integer afterInventory;
    private Integer correctionQty;
    private BigDecimal beforeRevenue;
    private BigDecimal afterRevenue;
    private BigDecimal correctionRevenue;
    private String correctionType;
    private String reason;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operateTime;
    private String status;
    private LocalDateTime createdTime;
}
