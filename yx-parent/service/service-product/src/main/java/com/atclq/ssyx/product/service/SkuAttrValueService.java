package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SkuAttrValue> findBySkuId(Long skuId);
}
