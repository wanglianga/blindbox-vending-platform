package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("machine_runtime_log")
public class MachineRuntimeLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long machineId;

    private String eventType;

    private LocalDateTime eventTime;

    private Integer durationSeconds;

    private String remark;

    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
