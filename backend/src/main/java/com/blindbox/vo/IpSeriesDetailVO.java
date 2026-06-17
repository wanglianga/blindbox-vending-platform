package com.blindbox.vo;

import com.blindbox.entity.BlindboxSku;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IpSeriesDetailVO {

    private Long id;
    private String name;
    private Long supplierId;
    private String supplierName;
    private String description;
    private String coverImage;
    private BigDecimal price;
    private Integer totalSkus;
    private String hiddenRule;
    private Integer status;
    private LocalDateTime createdTime;
    private List<BlindboxSku> skuList;
}
