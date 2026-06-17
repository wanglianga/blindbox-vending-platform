package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("replenishment_route")
public class ReplenishmentRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String routeName;

    private Long replenisherId;

    private String mallIds;

    private Integer machineCount;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
