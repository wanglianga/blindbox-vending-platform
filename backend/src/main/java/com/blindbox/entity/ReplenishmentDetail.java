package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("replenishment_detail")
public class ReplenishmentDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    private Long skuId;

    private Integer gridNo;

    private Integer replenishQty;

    private Integer beforeQty;

    private Integer afterQty;

    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
