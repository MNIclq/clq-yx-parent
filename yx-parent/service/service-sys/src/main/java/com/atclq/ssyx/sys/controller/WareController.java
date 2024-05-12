package com.atclq.ssyx.sys.controller;

import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.sys.Ware;
import com.atclq.ssyx.sys.service.WareService;
import com.atclq.ssyx.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "仓库管理接口")
@RestController
@RequestMapping("/admin/sys/ware")
//@CrossOrigin    //允许跨域请求          后面已使用网关替代
@CrossOrigin(origins = "*", maxAge = 3600)
public class WareController {

    @Autowired
    private WareService wareService;

    @ApiOperation("分页查询仓库")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              WareQueryVo wareQueryVo){
        Page<Ware> warePage = new Page<>(page, limit);
        IPage<Ware> wareIPage = wareService.selectWarePage(warePage, wareQueryVo);
        return Result.ok(wareIPage);
    }

    @ApiOperation("根据id查询仓库")
    @GetMapping("{id}")
    public Result getWareById(@PathVariable Long id){
        Ware ware = wareService.getById(id);
        return Result.ok(ware);
    }

    @ApiOperation("新增仓库")
    @PostMapping("save")
    public Result save(@RequestBody Ware ware) {
        wareService.save(ware);
        return Result.ok(null);
    }

    @ApiOperation("修改仓库")
    @PutMapping("update")
    public Result updateWare(@RequestBody Ware ware) {
        wareService.updateById(ware);
        return Result.ok(null);
    }

    @ApiOperation("根据id删除仓库")
    @DeleteMapping("remove/{id}")
    public Result removeWareById(@PathVariable Long id) {
        wareService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("根据ids批量删除仓库")
    @DeleteMapping("batchRemove")
    public Result batchRemoveWareByIds(Long[] ids) {
        wareService.removeByIds(Arrays.asList(ids));
        return Result.ok(null);
    }

    @ApiOperation("获取全部仓库")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Ware> wareList = wareService.list();
        return Result.ok(wareList);
    }
}
