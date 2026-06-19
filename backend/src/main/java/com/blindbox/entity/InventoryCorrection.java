package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inventory_correction")
public class InventoryCorrection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String correctionNo;

    private Long stuckRecordId;

    private String stuckRecordNo;

    private Long machineId;

    private String machineCode;

    private Integer gridNo;

    private Long skuId;

    private String skuName;

    private Integer beforeInventory;

    private Integer afterInventory;

    private Integer correctionQty;

    private BigDecimal beforeRevenue;

    private BigDecimal afterRevenue;

    private BigDecimal correctionRevenue;

    private String correctionType;

    private String reason;

    private Long operatorId;

    private LocalDateTime operateTime;

    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
