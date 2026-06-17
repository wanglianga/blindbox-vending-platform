package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.IpSeries;
import com.blindbox.vo.IpSeriesDetailVO;

public interface IpSeriesService extends IService<IpSeries> {

    IPage<IpSeries> getSeriesPage(String name, Integer status, Integer pageNum, Integer pageSize);

    IpSeriesDetailVO getSeriesDetail(Long id);

    void addSeries(IpSeries ipSeries);

    void updateSeries(IpSeries ipSeries);

    void toggleStatus(Long id, Integer status);

    void setHiddenRule(Long id, String hiddenRule);
}
