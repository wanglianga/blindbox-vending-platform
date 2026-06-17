package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuProbabilityVO {

    private Long skuId;
    private String skuName;
    private String skuCode;
    private String imageUrl;
    private Integer isHidden;
    private Integer stockCount;
    private BigDecimal actualProbability;
    private BigDecimal displayProbability;
}
