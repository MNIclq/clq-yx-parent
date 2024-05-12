package com.atclq.ssyx.sys.service;

import com.atclq.ssyx.model.sys.RegionWare;
import com.atclq.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


public interface RegionWareService extends IService<RegionWare> {
    IPage<RegionWare> selectRegionWarePage(Page<RegionWare> regionWarePage, RegionWareQueryVo regionWareQueryVo);

    void saveRegionWare(RegionWare regionWare);

    void updateStatus(Long id, Integer status);
}
