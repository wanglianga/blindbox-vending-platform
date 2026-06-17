package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cs_ticket")
public class CsTicket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ticketNo;

    private Long userId;

    private String userPhone;

    private Long orderId;

    private String orderNo;

    private String ticketType;

    private String title;

    private String description;

    private String status;

    private String priority;

    private Long handlerId;

    private String handleResult;

    private LocalDateTime handleTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
