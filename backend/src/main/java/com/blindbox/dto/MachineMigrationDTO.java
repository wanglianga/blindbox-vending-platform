package com.blindbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MachineMigrationDTO {

    @NotNull(message = "机器ID不能为空")
    private Long machineId;

    @NotNull(message = "新商场ID不能为空")
    private Long newMallId;

    private String newLocationDetail;

    @NotNull(message = "新扣点比例不能为空")
    private BigDecimal newCommissionRate;

    @NotBlank(message = "迁移原因不能为空")
    private String migrationReason;

    @NotNull(message = "迁移开始时间不能为空")
    private LocalDateTime migrationStartTime;

    private LocalDateTime migrationEndTime;

    private Integer downtimeMinutes;

    private BigDecimal downtimeLoss;

    private String lossRemark;

    private List<MigrationInventoryItemDTO> inventoryItems;
}
