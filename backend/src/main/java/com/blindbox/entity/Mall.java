package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mall")
public class Mall {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String contactName;

    private String contactPhone;

    private String address;

    private BigDecimal commissionRate;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
