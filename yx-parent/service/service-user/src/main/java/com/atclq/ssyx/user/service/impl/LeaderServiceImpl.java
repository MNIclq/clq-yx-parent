package com.atclq.ssyx.user.service.impl;

import com.atclq.ssyx.model.sys.Region;
import com.atclq.ssyx.model.user.Leader;
import com.atclq.ssyx.user.mapper.LeaderMapper;
import com.atclq.ssyx.user.mapper.RegionMapper;
import com.atclq.ssyx.user.service.LeaderService;
import com.atclq.ssyx.vo.user.LeaderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LeaderServiceImpl extends ServiceImpl<LeaderMapper, Leader> implements LeaderService {

    @Autowired
    private LeaderMapper leaderMapper;

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public IPage<Leader> selectCheckPage(Page<Leader> pageParam, LeaderQueryVo leaderQueryVo) {
        LambdaQueryWrapper<Leader> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Leader::getCheckStatus, 0);
        
        if(leaderQueryVo != null) {
            if(!StringUtils.isEmpty(leaderQueryVo.getKeyword())) {
                wrapper.like(Leader::getName, leaderQueryVo.getKeyword());
            }
        }
        
        IPage<Leader> page = leaderMapper.selectPage(pageParam, wrapper);
        setRegionName(page.getRecords());
        return page;
    }

    @Override
    public IPage<Leader> selectPage(Page<Leader> pageParam, LeaderQueryVo leaderQueryVo) {
        LambdaQueryWrapper<Leader> wrapper = new LambdaQueryWrapper<>();
        
        if(leaderQueryVo != null) {
            if(leaderQueryVo.getCheckStatus() != null) {
                wrapper.eq(Leader::getCheckStatus, leaderQueryVo.getCheckStatus());
            }
            if(!StringUtils.isEmpty(leaderQueryVo.getKeyword())) {
                wrapper.like(Leader::getName, leaderQueryVo.getKeyword());
            }
        }
        
        IPage<Leader> page = leaderMapper.selectPage(pageParam, wrapper);
        setRegionName(page.getRecords());
        return page;
    }

    private void setRegionName(List<Leader> leaderList) {
        for(Leader leader : leaderList) {
            if(leader.getRegionId() != null) {
                Region region = regionMapper.selectById(leader.getRegionId());
                if(region != null) {
                    leader.setRegionName(region.getName());
                }
            }
        }
    }

    @Override
    public void check(Long id, Integer status) {
        Leader leader = leaderMapper.selectById(id);
        if(leader != null) {
            leader.setCheckStatus(status);
            leaderMapper.updateById(leader);
        }
    }
}
