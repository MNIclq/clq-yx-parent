package com.atclq.ssyx.home.service.impl;

import com.atclq.ssyx.client.activity.ActivityFeignClient;
import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.client.search.SkuFeignClient;
import com.atclq.ssyx.client.user.UserFeignClient;
import com.atclq.ssyx.home.service.ItemService;
import com.atclq.ssyx.vo.product.SkuInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SkuFeignClient skuFeignClient;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @Override
    public Map<String, Object> item(Long skuId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        //sku查询商品信息
        CompletableFuture<SkuInfoVo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //通过service-product-client模块的productFeignClient远程调用service-product模块的ProductInnerController中的getSkuInfoVo()方法，获取sku商品信息
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);//TODO: 通过远程调用获取sku商品信息
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        },threadPoolExecutor);

        //sku对应优惠卷信息查询
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            //通过service-activity-client模块的ActivityFeignClient远程调用service-activity模块的ActivityInfoApiController中的findActivityAndCoupon()方法，根据skuId+userId获取sku对应优惠卷信息（其中又涉及到其他服务之间的远程调用）    优惠卷是针对某个用户使用的，所以不仅需要skuId，还需要用户id
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(skuId, userId);//TODO: 通过远程调用获取sku对应优惠卷信息
            result.putAll(activityMap);
        },threadPoolExecutor);

        //更新商品的热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            //通过service-search-client模块的SkuFeignClient远程调用service-search模块的SkuApiController中的incrHotScore()方法，更新商品的热度
            skuFeignClient.incrHotScore(skuId);//TODO: 通过远程调用更新商品的热度
        },threadPoolExecutor);

        //组合异步任务，等待所有任务完成
        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                activityCompletableFuture,
                hotCompletableFuture
        ).join();

        return result;
    }
}
