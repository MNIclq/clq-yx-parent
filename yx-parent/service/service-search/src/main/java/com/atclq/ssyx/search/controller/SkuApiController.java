package com.atclq.ssyx.search.controller;

import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.search.SkuEs;
import com.atclq.ssyx.search.service.SkuService;
import com.atclq.ssyx.vo.search.SkuEsQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/sku")
//@CrossOrigin          后面已使用网关替代
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    @ApiOperation(value = "上架商品")
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    @ApiOperation(value = "下架商品")
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }



    @ApiOperation(value = "获取爆品商品")
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }



    @ApiOperation(value = "查询分类下的商品")
    @GetMapping("{page}/{limit}")
    public Result list(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "searchParamVo", value = "查询对象", required = false)
            SkuEsQueryVo skuEsQueryVo) {
        //创建Pageable对象，0代表第一页，limit代表每页记录数  用于分页查询
        Pageable pageable = PageRequest.of(page-1, limit);
        Page<SkuEs> pageModel =  skuService.search(pageable, skuEsQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "根据skuId更新商品热度（incrHotScore）")
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        skuService.incrHotScore(skuId);
        return true;
    }

}
