package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.BlindboxSku;

import java.math.BigDecimal;

public interface BlindboxSkuService extends IService<BlindboxSku> {

    IPage<BlindboxSku> getSkuPage(Long seriesId, Integer isHidden, Integer pageNum, Integer pageSize);

    BlindboxSku getSkuDetail(Long id);

    void addSku(BlindboxSku sku);

    void updateSku(BlindboxSku sku);

    void deleteSku(Long id);

    void setProbability(Long id, BigDecimal probability);
}
