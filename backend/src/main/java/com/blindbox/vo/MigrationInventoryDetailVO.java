package com.blindbox.vo;

import lombok.Data;

@Data
public class MigrationInventoryDetailVO {

    private Long id;
    private Long skuId;
    private String skuName;
    private String skuCode;
    private String skuImage;
    private Long seriesId;
    private String seriesName;
    private Integer qtyBefore;
    private Integer qtyAfter;
    private Integer qtyTransferred;
    private Integer qtyLost;
    private String transferStatus;
    private String remark;
}
