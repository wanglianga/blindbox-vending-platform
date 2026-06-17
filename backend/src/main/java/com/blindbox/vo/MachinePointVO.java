package com.blindbox.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MachinePointVO {

    private Long id;
    private String machineCode;
    private String machineName;
    private Long mallId;
    private String mallName;
    private Long operatorId;
    private String operatorName;
    private String locationDetail;
    private String ipAddress;
    private Integer gridCount;
    private String status;
    private LocalDateTime lastOnlineTime;
    private Long currentSeriesId;
    private String seriesName;
    private LocalDateTime createdTime;
}
