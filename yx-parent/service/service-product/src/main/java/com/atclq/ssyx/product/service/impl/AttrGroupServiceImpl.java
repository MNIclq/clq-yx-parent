package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.AttrGroup;
import com.atclq.ssyx.product.mapper.AttrGroupMapper;
import com.atclq.ssyx.product.service.AttrGroupService;
import com.atclq.ssyx.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Autowired
    private AttrGroupMapper attrGroupMapper;

    @Override
    public IPage<AttrGroup> selectAttrGroupPage(Page<AttrGroup> attrGroupPage, AttrGroupQueryVo attrGroupQueryVo) {
        String name = attrGroupQueryVo.getName();

        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        if (name!= null &&!name.isEmpty()) {
            wrapper.like(AttrGroup::getName, name);
        }
        IPage<AttrGroup> attrGroupIpage = attrGroupMapper.selectPage(attrGroupPage, wrapper);

        return attrGroupIpage;
    }

    @Override
    public List<AttrGroup> findAllList() {
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(AttrGroup::getSort);
        List<AttrGroup> attrGroupList = attrGroupMapper.selectList(wrapper);
        return attrGroupList;
    }
}
