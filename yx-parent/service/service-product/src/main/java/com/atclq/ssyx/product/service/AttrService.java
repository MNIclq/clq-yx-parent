package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.Attr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface AttrService extends IService<Attr> {


    List<Attr> getByGroupId(Long[] groupId);
}
