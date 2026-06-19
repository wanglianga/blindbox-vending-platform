package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MachineMigrationDetailVO {

    private Long id;
    private String migrationNo;
    private Long machineId;
    private String machineCode;
    private String machineName;
    private Long oldMallId;
    private String oldMallName;
    private String oldLocationDetail;
    private BigDecimal oldCommissionRate;
    private Long newMallId;
    private String newMallName;
    private String newLocationDetail;
    private BigDecimal newCommissionRate;
    private String migrationReason;
    private LocalDateTime migrationStartTime;
    private LocalDateTime migrationEndTime;
    private Integer downtimeMinutes;
    private BigDecimal downtimeLoss;
    private String lossRemark;
    private Integer totalInventoryBefore;
    private Integer totalInventoryAfter;
    private Integer transferredInventory;
    private Integer lostInventory;
    private String inventoryTransferStatus;
    private String status;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdTime;
    private List<MigrationInventoryDetailVO> inventoryDetails;
}
