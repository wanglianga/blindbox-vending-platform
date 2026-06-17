package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DrawResultVO {

    private String orderNo;
    private Long skuId;
    private String skuName;
    private String skuCode;
    private String skuImage;
    private Integer isHidden;
    private Integer gridNo;
    private BigDecimal payAmount;
    private String payStatus;
    private String shipStatus;
    private String motorStatus;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
}
