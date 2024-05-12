package com.atclq.ssyx.sys.service.impl;

import com.atclq.ssyx.model.sys.Ware;
import com.atclq.ssyx.sys.mapper.WareMapper;
import com.atclq.ssyx.sys.service.WareService;
import com.atclq.ssyx.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware> implements WareService {

    @Autowired
    private WareMapper wareMapper;

    @Override
    public IPage<Ware> selectWarePage(Page<Ware> warePage, WareQueryVo wareQueryVo) {
        String name = wareQueryVo.getName();

        LambdaQueryWrapper<Ware> wrapper = new LambdaQueryWrapper<>();
        if (name!= null &&!name.equals("")) {
            wrapper.like(Ware::getName, name);
        }

        IPage<Ware> wareIPage =  wareMapper.selectPage(warePage, wrapper);

        return wareIPage;
    }
}
