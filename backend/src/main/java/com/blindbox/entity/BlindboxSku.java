package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("blindbox_sku")
public class BlindboxSku {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long seriesId;

    private String skuName;

    private String skuCode;

    private String imageUrl;

    private Integer isHidden;

    private BigDecimal probability;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
