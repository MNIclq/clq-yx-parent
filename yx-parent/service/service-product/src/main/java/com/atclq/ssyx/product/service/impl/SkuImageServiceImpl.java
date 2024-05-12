package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.SkuImage;
import com.atclq.ssyx.product.mapper.SkuImageMapper;
import com.atclq.ssyx.product.service.SkuImageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Override
    public List<SkuImage> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuImage::getSkuId, skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);
        if (skuImages != null && !skuImages.isEmpty()) {
            return skuImages;
        }
        return null;
    }
}
