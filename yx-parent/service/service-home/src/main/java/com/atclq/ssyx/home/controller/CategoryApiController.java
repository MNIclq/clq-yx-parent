package com.atclq.ssyx.home.controller;

import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.home.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "商品分类")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation("获取全部商品分类信息")
    @GetMapping("category")
    public Result index() {
        //通过service-product-feign模块的ProductFeignClient接口远程调用service-product模块的ProductController接口中的findAllCategoryList()方法，获取商品分类列表
        return Result.ok(productFeignClient.findAllCategoryList());
    }
}
