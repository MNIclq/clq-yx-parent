package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.AttrGroup;
import com.atclq.ssyx.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface AttrGroupService extends IService<AttrGroup> {

    IPage<AttrGroup> selectAttrGroupPage(Page<AttrGroup> attrGroupPage, AttrGroupQueryVo attrGroupQueryVo);

    List<AttrGroup> findAllList();
}
