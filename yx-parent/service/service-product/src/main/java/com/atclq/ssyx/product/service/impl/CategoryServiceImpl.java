package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.product.mapper.CategoryMapper;
import com.atclq.ssyx.product.service.CategoryService;
import com.atclq.ssyx.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public IPage<Category> selectCategoryPage(Page<Category> categoryPage, CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (name!= null && !name.isEmpty()) {
            wrapper.like(Category::getName, name);
        }

        IPage<Category> categoryIPage = categoryMapper.selectPage(categoryPage, wrapper);

        return categoryIPage;
    }

    @Override
    public List<Category> findAllList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        return categoryList;
    }

    @Override
    public List<Category> findCategoryList(List<Long> categoryIdList) {
        List<Category> categoryList = categoryMapper.selectBatchIds(categoryIdList);//通过mapper层调用BaseMapper的selectBatchIds方法
//        List<Category> categoryList = this.listByIds(categoryIdList);//"this" 关键字代表当前类的实例，可以直接调用service层IService的listByIds方法
        return categoryList;
    }

}
