package com.atclq.ssyx.sys.service;

import com.atclq.ssyx.model.sys.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RegionService extends IService<Region> {
    List<Region> findRegionByKeyword(String keyword);

    List<Region> findByParentId(Long parentId);
}
