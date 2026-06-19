package com.blindbox.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MigrationInventoryItemDTO {

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @NotNull(message = "迁移后数量不能为空")
    private Integer qtyAfter;

    private String remark;
}
