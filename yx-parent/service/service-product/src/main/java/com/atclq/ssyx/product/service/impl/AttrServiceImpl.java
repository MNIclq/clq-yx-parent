package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.Attr;
import com.atclq.ssyx.product.mapper.AttrMapper;
import com.atclq.ssyx.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    @Autowired
    private AttrMapper attrMapper;

    @Override
    public List<Attr> getByGroupId(Long[] groupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Attr::getAttrGroupId, groupId);

        List<Attr> attrList = attrMapper.selectList(wrapper);

        return attrList;
    }
}
