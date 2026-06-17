package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.ReplenishmentRoute;

public interface ReplenishmentRouteService extends IService<ReplenishmentRoute> {

    IPage<ReplenishmentRoute> getRoutePage(String routeName, Integer pageNum, Integer pageSize);

    ReplenishmentRoute getRouteDetail(Long id);

    void addRoute(ReplenishmentRoute route);

    void updateRoute(ReplenishmentRoute route);

    void deleteRoute(Long id);
}
