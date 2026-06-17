package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.ReplenishmentRoute;
import com.blindbox.mapper.ReplenishmentRouteMapper;
import com.blindbox.service.ReplenishmentRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplenishmentRouteServiceImpl extends ServiceImpl<ReplenishmentRouteMapper, ReplenishmentRoute> implements ReplenishmentRouteService {

    @Override
    public IPage<ReplenishmentRoute> getRoutePage(String routeName, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ReplenishmentRoute> wrapper = new LambdaQueryWrapper<>();
        if (routeName != null && !routeName.isEmpty()) {
            wrapper.like(ReplenishmentRoute::getRouteName, routeName);
        }
        wrapper.orderByAsc(ReplenishmentRoute::getSortOrder);

        Page<ReplenishmentRoute> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public ReplenishmentRoute getRouteDetail(Long id) {
        ReplenishmentRoute route = baseMapper.selectById(id);
        if (route == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return route;
    }

    @Override
    public void addRoute(ReplenishmentRoute route) {
        baseMapper.insert(route);
    }

    @Override
    public void updateRoute(ReplenishmentRoute route) {
        ReplenishmentRoute exist = baseMapper.selectById(route.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.updateById(route);
    }

    @Override
    public void deleteRoute(Long id) {
        ReplenishmentRoute exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.deleteById(id);
    }
}
