package com.atclq.ssyx.common.auth;

import com.atclq.ssyx.common.constant.RedisConst;
import com.atclq.ssyx.common.utils.JwtHelper;
import com.atclq.ssyx.vo.user.UserLoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;
    public UserLoginInterceptor(RedisTemplate redisTemplate) {//构造方法注入redisTemplate，不用@Autowired注入是因为拦截器没有被IOC容器管理
        this.redisTemplate = redisTemplate;
    }

    //登录拦截器 preHandle方法在请求处理之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.getUserLoginVo(request);

        return true;
    }

    private void getUserLoginVo(HttpServletRequest request) {
        //从请求头中获取token
        String token = request.getHeader("token");

        //判断token是否为空
        if (token != null) {
            //通过JwtHelper工具类从token中获取userId
            Long userId = JwtHelper.getUserId(token);

            //通过redisTemplate根据userId从redis中获取用户信息，得到UserLocalVo对象
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);

            //如果用户信息UserLocalVo对象不为空，从UserLocalVo对象中获取用户信息（用户id，提货点id），将用户信息（用户id，提货点id，还有UserLocalVo对象）放到ThreadLocal中
            if (userLoginVo != null) {
                AuthContextHolder.setUserId(userLoginVo.getUserId());
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserLoginVo(userLoginVo);
            }
        }
    }
}
