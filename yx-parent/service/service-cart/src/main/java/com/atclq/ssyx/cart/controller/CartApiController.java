package com.atclq.ssyx.cart.controller;

import com.atclq.ssyx.cart.service.CartInfoService;
import com.atclq.ssyx.client.activity.ActivityFeignClient;
import com.atclq.ssyx.common.auth.AuthContextHolder;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//TODO（使用redis的hash类型存储购物车）
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartInfoService cartInfoService;

    @Autowired
    private ActivityFeignClient activityFeignClient;


    /**
     * 新增购物车 把商品添加到购物车中，并使用redis的hash类型存储购物车
     * @param skuId
     * @param skuNum
     * @param request
     * @return
     */
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum,
                            HttpServletRequest request) {
        //获取userId（可以通过请求头获取，也可以通过TreadLocal的工具类AuthContextHolder(这个类定义在common模块的service-util子模块的auth包中)获取）//TODO AuthContextHolder是一个获取登录用户信息的工具类，从请求头中获取部分用户或管理员信息，专门为客户端服务，
//        Long userId = (Long) request.getAttribute("userId");//通过请求头获取
        // 如何获取userId
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.addToCart(skuId, userId, skuNum);
        return Result.ok(null);
    }

    /**
     * 根据skuId删除购物车中的商品
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request) {
        // 如何获取userId
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteCart(skuId, userId);
        return Result.ok(null);
    }

    /**
     * 根据userId获取购物车中的所有商品，清空购物车
     * @param request
     * @return
     */
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart(HttpServletRequest request){
        // 如何获取userId
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteAllCart(userId);
        return Result.ok(null);
    }

    /**
     * 根据skuId列表批量删除购物车中的商品
     * @param skuIdList
     * @param request
     * @return
     */
    @PostMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList, HttpServletRequest request){
        // 如何获取userId
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.batchDeleteCart(skuIdList, userId);
        return Result.ok(null);
    }



    //购物车列表
    @GetMapping("cartList")
    public Result cartList() {
        //获取userId
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);
        return Result.ok(cartInfoList);
    }

    /**
     * 查询带优惠卷的购物车（非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常复杂啊！！！！！！！！！！！！！！！！！！！！！）
     *
     * @return
     */
    @GetMapping("activityCartList")
    public Result activityCartList() {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);
        //通过service-activity-client的ActivityFeignClient远程调用service-activity模块的ProductInnerController中的findCartActivityAndCoupon()方法，获取购物车满足条件的促销与优惠券信息
        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderTradeVo);
    }

    /**
     * 1 根据skuId选中（操作redis）
     * @param skuId
     * @param isChecked
     * @return
     */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked) {
        //获取userId
        Long userId = AuthContextHolder.getUserId();
        //调用方法
        cartInfoService.checkCart(userId,skuId,isChecked);
        return Result.ok(null);
    }

    /**
     * 2 全选（操作redis）
     * @param isChecked
     * @return
     */
    @GetMapping("checkAllCart/{isChecked}")
    public Result checkAllCart(@PathVariable Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.checkAllCart(userId,isChecked);
        return Result.ok(null);
    }

    /**
     * 3 批量选中（操作redis）
     * @param skuIdList
     * @param isChecked
     * @return
     */
    @PostMapping("batchCheckCart/{isChecked}")
    public Result batchCheckCart(@RequestBody List<Long> skuIdList,
                                 @PathVariable Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.batchCheckCart(skuIdList,userId,isChecked);
        return Result.ok(null);
    }



    //获取当前用户购物车选中购物项
    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId) {
        return cartInfoService.getCartCheckedList(userId);
    }
}
