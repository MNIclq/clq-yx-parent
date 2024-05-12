package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.SkuPoster;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> findBySkuId(Long skuId);
}
