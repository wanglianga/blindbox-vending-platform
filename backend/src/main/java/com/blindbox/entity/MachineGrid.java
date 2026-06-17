package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("machine_grid")
public class MachineGrid {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long machineId;

    private Integer gridNo;

    private Long skuId;

    private String status;

    private LocalDateTime lastCheckTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
