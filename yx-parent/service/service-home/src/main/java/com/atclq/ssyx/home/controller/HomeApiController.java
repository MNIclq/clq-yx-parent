package com.atclq.ssyx.home.controller;

import com.atclq.ssyx.common.auth.AuthContextHolder;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.home.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "首页接口")
@RestController
@RequestMapping("api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;

    @ApiOperation("获取首页数据")
    @GetMapping("index")
    public Result index(HttpServletRequest request) {
        //获取userId（可以通过请求头获取，也可以通过TreadLocal的工具类AuthContextHolder(这个类定义在common模块的service-util子模块的auth包中)获取）
//        Long userId = (Long) request.getAttribute("userId");//通过请求头获取
        Long userId = AuthContextHolder.getUserId();//通过TreadLocal的工具类AuthContextHolder获取
        Map<String, Object> data = homeService.homeData(userId);
        return Result.ok(data);
    }

}
