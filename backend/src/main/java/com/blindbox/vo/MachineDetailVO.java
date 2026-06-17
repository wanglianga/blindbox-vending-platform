package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MachineDetailVO {

    private Long machineId;
    private String machineCode;
    private String machineName;
    private String status;
    private Long seriesId;
    private String seriesName;
    private String seriesCover;
    private BigDecimal price;
    private String hiddenRule;
    private Integer totalGridCount;
    private Integer filledGridCount;
    private Integer emptyGridCount;
    private List<SkuProbabilityVO> skuProbabilityList;
}
