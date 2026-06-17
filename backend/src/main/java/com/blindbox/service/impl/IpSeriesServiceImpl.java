package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.BlindboxSku;
import com.blindbox.entity.IpSeries;
import com.blindbox.entity.Supplier;
import com.blindbox.mapper.BlindboxSkuMapper;
import com.blindbox.mapper.IpSeriesMapper;
import com.blindbox.mapper.SupplierMapper;
import com.blindbox.service.IpSeriesService;
import com.blindbox.vo.IpSeriesDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IpSeriesServiceImpl extends ServiceImpl<IpSeriesMapper, IpSeries> implements IpSeriesService {

    private final BlindboxSkuMapper blindboxSkuMapper;
    private final SupplierMapper supplierMapper;

    @Override
    public IPage<IpSeries> getSeriesPage(String name, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<IpSeries> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(IpSeries::getName, name);
        }
        if (status != null) {
            wrapper.eq(IpSeries::getStatus, status);
        }
        wrapper.orderByDesc(IpSeries::getCreatedTime);

        Page<IpSeries> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IpSeriesDetailVO getSeriesDetail(Long id) {
        IpSeries series = baseMapper.selectById(id);
        if (series == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        IpSeriesDetailVO vo = new IpSeriesDetailVO();
        BeanUtils.copyProperties(series, vo);

        Supplier supplier = supplierMapper.selectById(series.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getName());
        }

        List<BlindboxSku> skuList = blindboxSkuMapper.selectList(
                new LambdaQueryWrapper<BlindboxSku>()
                        .eq(BlindboxSku::getSeriesId, id)
                        .orderByAsc(BlindboxSku::getSortOrder)
        );
        vo.setSkuList(skuList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSeries(IpSeries ipSeries) {
        baseMapper.insert(ipSeries);
    }

    @Override
    public void updateSeries(IpSeries ipSeries) {
        IpSeries exist = baseMapper.selectById(ipSeries.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.updateById(ipSeries);
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        IpSeries exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        exist.setStatus(status);
        baseMapper.updateById(exist);
    }

    @Override
    public void setHiddenRule(Long id, String hiddenRule) {
        IpSeries exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        exist.setHiddenRule(hiddenRule);
        baseMapper.updateById(exist);
    }
}
