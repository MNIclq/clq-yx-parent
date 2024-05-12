package com.atclq.ssyx.cart.service.impl;

import com.atclq.ssyx.cart.service.CartInfoService;
import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.common.constant.RedisConst;
import com.atclq.ssyx.common.exception.SsyxException;
import com.atclq.ssyx.common.result.ResultCodeEnum;
import com.atclq.ssyx.enums.SkuType;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;


    //结合Redis常量类RedisConst，构建购物车在redis的key
    private String getCartKey(Long userId) {
        // user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    //设置key 过期时间
    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key,RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 新增购物车 把商品添加到购物车中，并使用redis的hash类型存储购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addToCart(Long skuId, Long userId, Integer skuNum) {
        //1 购物车的数据都存储在redis中，所以需要先根据key获取redis中的购物车数据
        ////这个key中包含userId
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);

        //2 根据第一步查出来的结果是skuId+skuNum关系，判断结果里是否包含参数skuId，即判断是否是第一次添加这个商品到购物车
        CartInfo cartInfo = null;
        if(hashOperations.hasKey(skuId.toString())){
            //2.1 如果结果中包含参数skuId，则不是第一次添加，根据skuId获取购物车对象，并更新其中商品的数量
            cartInfo = hashOperations.get(skuId.toString());
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if(currentSkuNum < 1){
                return;
            }
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);

            //判断商品数量是否大于限购数量
            Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit){
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            //更新其他值
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        }else{
            //2.2 如果结果中不包含参数skuId，则是第一次添加，直接新增商品到购物车中，并设置商品的数量为skuNum=1
            skuNum=1;

            //通过service-product-client的ProductFeignClient远程调用service-product模块的ProductInnerController中的getSkuInfo()方法，根据skuId获取skuInfo对象
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if(skuInfo == null) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }

            //封装cartInfo对象
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());

        }

        //3 更新redis缓存
        hashOperations.put(skuId.toString(),cartInfo);

        //4 设置redis缓存的有效时间，如果超过有效时间不支付就删除
        this.setCartKeyExpire(cartKey);

    }

    /**
     * 根据skuId删除购物车中的商品
     * @param skuId
     * @param userId
     */
    @Override
    public void deleteCart(Long skuId, Long userId) {
        BoundHashOperations<String,String,CartInfo> hashOperations =
                redisTemplate.boundHashOps(this.getCartKey(userId));
        if(hashOperations.hasKey(skuId.toString())) {
            hashOperations.delete(skuId.toString());
        }
    }

    /**
     * 根据userId获取购物车中的所有商品，清空购物车
     * @param userId
     */
    @Override
    public void deleteAllCart(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();
        for (CartInfo cartInfo:cartInfoList) {
            hashOperations.delete(cartInfo.getSkuId().toString());
        }
    }

    /**
     * 根据skuId列表批量删除购物车中的商品
     * @param skuIdList
     * @param userId
     */
    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            hashOperations.delete(skuId.toString());
        });
    }


    /**
     * 根据userId获取购物车列表
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        //判断userId
        if(StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }
        //从redis获取购物车数据
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);//获取这个哈希类型的所有值
        cartInfoList = boundHashOperations.values();

        if(!CollectionUtils.isEmpty(cartInfoList)) {
            //根据商品添加时间，降序排序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());//如果o1的创建时间早于o2的创建时间，那么compareTo方法会返回一个负数；如果o1的创建时间晚于o2的创建时间，那么compareTo方法会返回一个正数；如果两者的创建时间相同，那么compareTo方法会返回0
                }
            });
        }
        return cartInfoList;
    }

    //1 根据skuId选中
    @Override
    public void checkCart(Long userId, Long skuId, Integer isChecked) {
        //获取redis的key
        String cartKey = this.getCartKey(userId);
        //cartKey获取field-value
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        //根据field（skuId）获取value（CartInfo）
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if(cartInfo != null) {
            cartInfo.setIsChecked(isChecked);
            //更新
            boundHashOperations.put(skuId.toString(),cartInfo);
            //设置key过期时间
            this.setCartKeyExpire(cartKey);
        }
    }

    //2 全选
    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        cartInfoList.forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
        });
        this.setCartKeyExpire(cartKey);
    }

    //3 批量选中
    @Override
    public void batchCheckCart(List<Long> skuIdList,
                               Long userId,
                               Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
        });
        this.setCartKeyExpire(cartKey);
    }

    //获取当前用户购物车选中购物项
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        //isChecked = 1购物项选中
        List<CartInfo> cartInfoListNew = cartInfoList.stream()
                .filter(cartInfo -> {
                    return cartInfo.getIsChecked().intValue() == 1;
                }).collect(Collectors.toList());
        return cartInfoListNew;
    }

    //根据userId删除选中购物车记录
    @Override
    public void deleteCartChecked(Long userId) {
        //根据userid查询选中购物车记录
        List<CartInfo> cartInfoList = this.getCartCheckedList(userId);

        //查询list数据处理，得到skuId集合
        List<Long> skuIdList = cartInfoList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

        //构建redis的key值
        // hash类型 key filed-value
        String cartKey = this.getCartKey(userId);

        //根据key查询filed-value结构
        BoundHashOperations<String,String,CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);

        //根据filed（skuId）删除redis数据
        skuIdList.forEach(skuId -> {
            hashOperations.delete(skuId.toString());
        });
    }

}
