package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("probability_audit_log")
public class ProbabilityAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long machineId;

    private Long seriesId;

    private String auditType;

    private String beforeAudit;

    private String afterAudit;

    private Integer isConsistent;

    private Long operatorId;

    private LocalDateTime createdTime;
}
