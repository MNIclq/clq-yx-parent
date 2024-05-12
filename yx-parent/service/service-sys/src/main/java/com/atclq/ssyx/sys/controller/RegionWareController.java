package com.atclq.ssyx.sys.controller;

import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.sys.RegionWare;
import com.atclq.ssyx.sys.service.RegionWareService;
import com.atclq.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "区域仓库管理接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
//@CrossOrigin    //允许跨域请求          后面已使用网关替代
public class RegionWareController {

    @Autowired
    private RegionWareService regionWareService;

    @ApiOperation("分页查询区域仓库列表")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable("page") Long page,
                              @PathVariable("limit") Long limit,
                              RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> RegionWarePage = new Page<>(page, limit);
        IPage<RegionWare> RegionWareIPage = regionWareService.selectRegionWarePage(RegionWarePage, regionWareQueryVo);

        return Result.ok(RegionWareIPage);
    }

    @ApiOperation("根据id查询区域仓库")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        RegionWare regionWare = regionWareService.getById(id);
        return Result.ok(regionWare);
    }

    @ApiOperation("新增区域仓库（先根据region_id（因为region_id唯一）查询区域表中是否存在该区域，如果数据库中不存在该区域，则不能新增，抛出异常；否则根据region_id（因为region_id唯一）查询区域仓库表中是否已存在该区域仓库，如果数据库中已存在该区域仓库，则不能新增，抛出异常；否则新增区域仓库")
    @PostMapping("save")
    public Result saveRegionWare(@RequestBody RegionWare regionWare) {
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }

    @ApiOperation("更改区域仓库状态")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        regionWareService.updateStatus(id, status);
        return Result.ok(null);
    }

    @ApiOperation("根据id删除区域仓库")
    @DeleteMapping("remove/{id}")
    public Result removeRegionWareById(@PathVariable("id") Long id) {
        regionWareService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除区域仓库")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        regionWareService.removeByIds(ids);
        return Result.ok(null);
    }
}
