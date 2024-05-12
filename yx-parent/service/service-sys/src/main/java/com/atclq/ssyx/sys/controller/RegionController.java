package com.atclq.ssyx.sys.controller;

import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.sys.Region;
import com.atclq.ssyx.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "区域管理接口")
@RestController
@RequestMapping("/admin/sys/region")
//@CrossOrigin    //允许跨域请求      后面已使用网关替代
//@SuppressWarnings({"unchecked", "rawtypes"})
public class RegionController {

    @Autowired
    private RegionService regionService;

    @ApiOperation("根据关键词查询区域")
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable String keyword){
        List<Region> regions = regionService.findRegionByKeyword(keyword);
        return Result.ok(regions);
    }

    @ApiOperation("根据ParentId查询区域")
    @GetMapping("findByParentId/{parentId}")
    public Result findByParentId(@PathVariable Long parentId){
        List<Region> regions = regionService.findByParentId(parentId);
        return Result.ok(regions);
    }
}
