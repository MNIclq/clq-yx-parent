package com.atclq.ssyx.home.service.impl;

import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.client.search.SkuFeignClient;
import com.atclq.ssyx.client.user.UserFeignClient;
import com.atclq.ssyx.home.service.HomeService;
import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.model.search.SkuEs;
import com.atclq.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SkuFeignClient skuFeignClient;


    @Override
    public Map<String, Object> homeData(Long userId) {
        Map<String, Object> resultMap = new HashMap<>();

        //1 根据userId获取当前登录用户提货的地址信息 LeaderAddressVo对象
        //通过service-user-client模块中的UserFeignClient远程调用service-user模块中的接口 获取
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        resultMap.put("leaderAddressVo", leaderAddressVo);

        //2 获取所有分类
        //通过service-product-client模块中的ProductFeignClient远程调用service-product模块中的接口 获取
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        resultMap.put("categoryList", categoryList);

        //3 获取新人专享商品
        //通过service-product-client模块中的ProductFeignClient远程调用service-product模块中的接口 获取
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        resultMap.put("newPersonSkuInfoList", newPersonSkuInfoList);

        //4 获取爆款商品
        //通过service-search-client模块中的SearchFeignClient远程调用service-search模块中的接口 获取
        //在ES中查询出热销商品，并根据销量排序，ES中有个字段是score（评分），按照score字段进行降序排序，选取评分高的商品作为爆款商品
        List<SkuEs> hotSkuList = skuFeignClient.findHotSkuList();
        resultMap.put("hotSkuList", hotSkuList);

        //5 将数据封装到map中并返回

        return null;
    }
}
