package com.atclq.ssyx.sys.service.impl;

import com.atclq.ssyx.common.exception.SsyxException;
import com.atclq.ssyx.common.result.ResultCodeEnum;
import com.atclq.ssyx.model.sys.RegionWare;
import com.atclq.ssyx.sys.mapper.RegionWareMapper;
import com.atclq.ssyx.sys.service.RegionWareService;
import com.atclq.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    @Autowired
    private RegionWareMapper regionWareMapper;

    @Override
    public IPage<RegionWare> selectRegionWarePage(Page<RegionWare> regionWarePage, RegionWareQueryVo regionWareQueryVo) {
        String keyword = regionWareQueryVo.getKeyword();

        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if (keyword!= null && !keyword.equals("")) {
            wrapper.like(RegionWare::getRegionName, keyword)
                    .or().
                    like(RegionWare::getWareName, keyword);
        }

        IPage<RegionWare> regionWareIPage = regionWareMapper.selectPage(regionWarePage, wrapper);

        return regionWareIPage;
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());

        Integer count = regionWareMapper.selectCount(wrapper);

        if(count > 0){
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }

        regionWareMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = regionWareMapper.selectById(id);
        regionWare.setStatus(status);
        regionWareMapper.updateById(regionWare);
    }
}
