package com.atclq.ssyx.home.controller;

import com.atclq.ssyx.common.auth.AuthContextHolder;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.home.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "商品详情")
@RestController
@RequestMapping("api/home")
public class ItemApiController {
    @Resource
    private ItemService itemService;

    @ApiOperation(value = "根据skuId获取sku详细信息（使用CompletableFuture类通过异步多线程并行实现）")
    @GetMapping("item/{id}")
    public Result index(@PathVariable Long skuId,HttpServletRequest request) {
        //获取userId（可以通过请求头获取，也可以通过TreadLocal的工具类AuthContextHolder(这个类定义在common模块的service-util子模块的auth包中)获取）
//        Long userId = (Long) request.getAttribute("userId");//通过请求头获取
        Long userId = AuthContextHolder.getUserId();//通过TreadLocal的工具类AuthContextHolder获取
        Map<String, Object> map = itemService.item(skuId, userId);
        return Result.ok(map);
    }
}
