package com.atclq.ssyx.product.controller;


import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.product.Attr;
import com.atclq.ssyx.product.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品属性 前端控制器
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Api(tags = "商品属性")
@RestController
@RequestMapping("/admin/product/attr")
//@CrossOrigin      后面已使用网关替代
public class AttrController {

    @Autowired
    private AttrService attrService;

    @ApiOperation("根据一组id批量查询")
    @GetMapping("/{groupId}")
    public Result getAttrsListByGroupId(@PathVariable Long[] groupId) {
        List<Attr> attrList = attrService.getByGroupId(groupId);
        return Result.ok(attrList);
    }

    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result getAttrById(@PathVariable Long id) {
        Attr attr = attrService.getById(id);
        return Result.ok(attr);
    }

    @ApiOperation("新增属性")
    @PostMapping("save")
    public Result saveAttr(@RequestBody Attr attr) {
        attrService.save(attr);
        return Result.ok(null);
    }

    @ApiOperation("修改属性")
    @PutMapping("update")
    public Result updateAttrById(@RequestBody Attr attr) {
        attrService.updateById(attr);
        return Result.ok(null);
    }

    @ApiOperation("删除属性")
    @DeleteMapping("remove/{id}")
    public Result deleteAttrById(@PathVariable Long id) {
        attrService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除属性")
    @DeleteMapping("batchRemove")
    public Result batchRemoveAttrByIds(@RequestBody List<Long> ids) {
        attrService.removeByIds(ids);
        return Result.ok(null);
    }
}

