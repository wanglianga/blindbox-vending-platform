package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("replenishment_plan")
public class ReplenishmentPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String planNo;

    private Long replenisherId;

    private Long machineId;

    private LocalDate planDate;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
