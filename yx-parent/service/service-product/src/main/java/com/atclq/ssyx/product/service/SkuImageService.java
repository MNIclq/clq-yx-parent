package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.SkuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface SkuImageService extends IService<SkuImage> {

    List<SkuImage> findBySkuId(Long skuId);
}
