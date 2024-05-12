package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品三级分类 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface CategoryService extends IService<Category> {

    IPage<Category> selectCategoryPage(Page<Category> categoryPage, CategoryQueryVo categoryQueryVo);

    List<Category> findAllList();

    List<Category> findCategoryList(List<Long> categoryIdList);
}
