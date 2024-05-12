package com.atclq.ssyx.common.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加自定义拦截器，设置路径
        registry.addInterceptor(
                new UserLoginInterceptor(redisTemplate))
                .addPathPatterns("/api/**")//设置自定义拦截器路径
                .excludePathPatterns("/api/user/weixin/wxLogin/*");//排除微信登录接口
        super.addInterceptors(registry);
    }
}