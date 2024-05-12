package com.atclq.ssyx.cart.service;

import com.atclq.ssyx.model.order.CartInfo;

import java.util.List;

public interface CartInfoService {

    //新增购物车 把商品添加到购物车中，并使用redis的hash类型存储购物车
    void addToCart(Long skuId, Long userId, Integer skuNum);

    //根据skuId删除购物车中的商品
    void deleteCart(Long skuId, Long userId);

    //根据userId获取购物车中的所有商品，清空购物车
    void deleteAllCart(Long userId);

    //根据skuId列表批量删除购物车中的商品
    void batchDeleteCart(List<Long> skuIdList, Long userId);



    //根据userId获取购物车列表
    List<CartInfo> getCartList(Long userId);

    //1 根据skuId选中
    void checkCart(Long userId, Long skuId, Integer isChecked);

    //2 全选
    void checkAllCart(Long userId, Integer isChecked);

    //3 批量选中
    void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked);

    //获取当前用户购物车选中购物项
    List<CartInfo> getCartCheckedList(Long userId);

    //根据userId删除选中购物车记录
    void deleteCartChecked(Long userId);
}
