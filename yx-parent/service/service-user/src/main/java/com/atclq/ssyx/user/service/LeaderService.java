package com.atclq.ssyx.user.service;

import com.atclq.ssyx.model.user.Leader;
import com.atclq.ssyx.vo.user.LeaderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LeaderService extends IService<Leader> {

    IPage<Leader> selectCheckPage(Page<Leader> pageParam, LeaderQueryVo leaderQueryVo);

    IPage<Leader> selectPage(Page<Leader> pageParam, LeaderQueryVo leaderQueryVo);

    void check(Long id, Integer status);
}
