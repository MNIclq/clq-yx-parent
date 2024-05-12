package com.atclq.ssyx.product.controller;


import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.product.service.CategoryService;
import com.atclq.ssyx.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 商品三级分类 前端控制器
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Api(tags = "商品三级分类")
@RestController
@RequestMapping("/admin/product/category")
//@CrossOrigin      后面已使用网关替代
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("分页查询")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                         @PathVariable Long limit,
                         CategoryQueryVo categoryQueryVo) {
        Page<Category> categoryPage = new Page<>(page, limit);
        IPage<Category> categoryIPage = categoryService.selectCategoryPage(categoryPage, categoryQueryVo);
        return Result.ok(categoryIPage);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.ok(category);
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public Result saveCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok(null);
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Result updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok(null);
    }

    @ApiOperation("删除")
    @DeleteMapping("/remove/{id}")
    public Result removeCategoryById(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batchRemove")
    public Result removeRowsCategoryByIds(@RequestBody List<Long> ids) {
        categoryService.removeByIds(ids);
        return Result.ok(null);
    }

    @ApiOperation("查找所有")
    @GetMapping("/findAllList")
    public Result findAllList() {
        List<Category> categoryList = categoryService.findAllList();
        return Result.ok(categoryList);
    }
}

