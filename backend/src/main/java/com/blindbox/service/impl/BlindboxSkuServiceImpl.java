package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.BlindboxSku;
import com.blindbox.entity.IpSeries;
import com.blindbox.mapper.BlindboxSkuMapper;
import com.blindbox.mapper.IpSeriesMapper;
import com.blindbox.service.BlindboxSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BlindboxSkuServiceImpl extends ServiceImpl<BlindboxSkuMapper, BlindboxSku> implements BlindboxSkuService {

    private final IpSeriesMapper ipSeriesMapper;

    @Override
    public IPage<BlindboxSku> getSkuPage(Long seriesId, Integer isHidden, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<BlindboxSku> wrapper = new LambdaQueryWrapper<>();
        if (seriesId != null) {
            wrapper.eq(BlindboxSku::getSeriesId, seriesId);
        }
        if (isHidden != null) {
            wrapper.eq(BlindboxSku::getIsHidden, isHidden);
        }
        wrapper.orderByAsc(BlindboxSku::getSortOrder);

        Page<BlindboxSku> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public BlindboxSku getSkuDetail(Long id) {
        BlindboxSku sku = baseMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return sku;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSku(BlindboxSku sku) {
        baseMapper.insert(sku);

        IpSeries series = ipSeriesMapper.selectById(sku.getSeriesId());
        if (series != null) {
            series.setTotalSkus(series.getTotalSkus() + 1);
            ipSeriesMapper.updateById(series);
        }
    }

    @Override
    public void updateSku(BlindboxSku sku) {
        BlindboxSku exist = baseMapper.selectById(sku.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.updateById(sku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(Long id) {
        BlindboxSku exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.deleteById(id);

        IpSeries series = ipSeriesMapper.selectById(exist.getSeriesId());
        if (series != null && series.getTotalSkus() > 0) {
            series.setTotalSkus(series.getTotalSkus() - 1);
            ipSeriesMapper.updateById(series);
        }
    }

    @Override
    public void setProbability(Long id, BigDecimal probability) {
        BlindboxSku exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        exist.setProbability(probability);
        baseMapper.updateById(exist);
    }
}
