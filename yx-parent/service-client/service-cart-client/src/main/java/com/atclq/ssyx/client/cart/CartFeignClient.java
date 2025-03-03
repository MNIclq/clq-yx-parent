package com.atclq.ssyx.client.cart;

import com.atclq.ssyx.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-cart")
public interface CartFeignClient {

    //获取当前用户购物车选中商品列表
    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);

}
