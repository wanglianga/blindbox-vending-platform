package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private Long machineId;
    private String machineName;
    private Long seriesId;
    private String seriesName;
    private Long userId;
    private BigDecimal payAmount;
    private String payType;
    private String payStatus;
    private LocalDateTime payTime;
    private Long drawSkuId;
    private String skuName;
    private String skuImage;
    private Integer isHidden;
    private Integer gridNo;
    private String shipStatus;
    private LocalDateTime shipTime;
    private String motorStatus;
    private String remark;
    private LocalDateTime createdTime;
}
