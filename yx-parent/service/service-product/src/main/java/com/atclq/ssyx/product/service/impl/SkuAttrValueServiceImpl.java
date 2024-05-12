package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.SkuAttrValue;
import com.atclq.ssyx.product.mapper.SkuAttrValueMapper;
import com.atclq.ssyx.product.service.SkuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spu属性值 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue> implements SkuAttrValueService {

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public List<SkuAttrValue> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuAttrValue::getSkuId, skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.selectList(wrapper);
        if (skuAttrValues!= null && !skuAttrValues.isEmpty()) {
            return skuAttrValues;
        }
        return null;
    }
}
