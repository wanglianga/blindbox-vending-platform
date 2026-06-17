package com.blindbox.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReplenishmentPlanVO {

    private Long id;
    private String planNo;
    private Long replenisherId;
    private String replenisherName;
    private Long machineId;
    private String machineName;
    private String machineCode;
    private LocalDate planDate;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String remark;
    private LocalDateTime createdTime;
}
