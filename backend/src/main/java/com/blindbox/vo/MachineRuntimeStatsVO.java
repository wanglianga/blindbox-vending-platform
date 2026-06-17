package com.blindbox.vo;

import lombok.Data;

@Data
public class MachineRuntimeStatsVO {

    private Long totalSeconds;
    private Long offlineSeconds;
    private Long faultSeconds;
    private Long onlineSeconds;
    private Double onlineRate;
}
