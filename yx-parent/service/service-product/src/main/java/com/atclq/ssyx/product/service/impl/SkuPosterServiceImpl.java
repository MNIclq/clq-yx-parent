package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.SkuPoster;
import com.atclq.ssyx.product.mapper.SkuPosterMapper;
import com.atclq.ssyx.product.service.SkuPosterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {

    @Autowired
    private SkuPosterMapper skuPosterMapper;

    @Override
    public List<SkuPoster> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPoster::getSkuId, skuId);
        List<SkuPoster> skuPosters = skuPosterMapper.selectList(wrapper);
        if (skuPosters != null && !skuPosters.isEmpty()) {
            return skuPosters;
        }
        return null;
    }
}
