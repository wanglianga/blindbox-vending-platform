package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("migration_inventory_detail")
public class MigrationInventoryDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long migrationId;

    private String migrationNo;

    private Long skuId;

    private String skuName;

    private String skuCode;

    private Long seriesId;

    private String seriesName;

    private Integer qtyBefore;

    private Integer qtyAfter;

    private Integer qtyTransferred;

    private Integer qtyLost;

    private String transferStatus;

    private String remark;

    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
