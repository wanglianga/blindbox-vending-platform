package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("machine_point")
public class MachinePoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String machineCode;

    private String machineName;

    private Long mallId;

    private Long operatorId;

    private String locationDetail;

    private String ipAddress;

    private Integer gridCount;

    private String status;

    private LocalDateTime lastOnlineTime;

    private Long currentSeriesId;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
