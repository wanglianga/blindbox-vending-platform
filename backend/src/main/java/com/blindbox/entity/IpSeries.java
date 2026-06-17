package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ip_series")
public class IpSeries {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long supplierId;

    private String description;

    private String coverImage;

    private BigDecimal price;

    private Integer totalSkus;

    private String hiddenRule;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
