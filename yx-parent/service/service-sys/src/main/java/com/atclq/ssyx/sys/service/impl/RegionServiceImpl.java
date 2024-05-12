package com.atclq.ssyx.sys.service.impl;


import com.atclq.ssyx.model.sys.Region;
import com.atclq.ssyx.sys.mapper.RegionMapper;
import com.atclq.ssyx.sys.service.RegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<Region> findRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Region::getName, keyword);
        List<Region> regions = regionMapper.selectList(wrapper);

        return regions;
    }

    @Override
    public List<Region> findByParentId(Long parentId) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getParentId, parentId);
        List<Region> regions = regionMapper.selectList(wrapper);

        return regions;
    }
}
