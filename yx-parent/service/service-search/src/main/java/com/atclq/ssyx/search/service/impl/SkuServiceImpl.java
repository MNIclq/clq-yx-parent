package com.atclq.ssyx.search.service.impl;

import com.atclq.ssyx.client.activity.ActivityFeignClient;
import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.common.auth.AuthContextHolder;
import com.atclq.ssyx.enums.SkuType;
import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.model.search.SkuEs;
import com.atclq.ssyx.search.repository.SkuRepository;
import com.atclq.ssyx.search.service.SkuService;
import com.atclq.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void upperSku(Long skuId) {
        //1 通过远程调用，根据skuId获取商品信息（包括sku信息和分类信息）
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        if(skuInfo == null) return;

        Category categoryInfo = productFeignClient.getCategory(skuInfo.getCategoryId());

        //2 将获取的数据封装到SkuEs对象中
        SkuEs skuEs = new SkuEs();
        //封装分类信息
        if(categoryInfo!= null) {
            skuEs.setCategoryId(categoryInfo.getId());
            skuEs.setCategoryName(categoryInfo.getName());
        }
        //封装sku信息
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(Objects.equals(skuInfo.getSkuType(), SkuType.COMMON.getCode())) {
            skuEs.setSkuType(0);//目前先全按照普通商品进行封装
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }
        //3 保存到ES中
        skuRepository.save(skuEs);
    }

    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        Pageable pageable = PageRequest.of(0, 10);//通过SpringData的Pageable对象来进行分页查询 0代表第一页
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);//这里的方法命名是按照SpringData的命名规范来命名的 find开头的表示查询，OrderBy表示排序，HotScoreDesc表示按照热销量，Desc表示倒序排序
        List<SkuEs> skuEsList = pageModel.getContent();

        return skuEsList;
    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        Page<SkuEs> pageModel = null;//结果

        //1 使用TreadLocal工具类AuthContextHolder获取当前登录用户的仓库id，向SkuEsQueryVo中设置wareId，即当前登录用户的仓库id
        //（在service-user模块的WeixinApiController中，已经获取当前登录用户信息，放到Redis里面，并设置了有效时间；而且在common的service-util模块中的UserLoginInterceptor类已经从Redis中获取当前登录用户信息(包括用户id即userId，仓库id即wareId)，并将其放到ThreadLocal中。所以这里直接使用TreadLocal工具类AuthContextHolder从ThreadLocal中获取当前登录用户的仓库id）
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        //2 调用SkuRepository的方法，根据SpringData的命名规范来定义方法，进行条件查询
        //2.1 判断skuEsQueryVo中的keyword是否为空
        if(skuEsQueryVo.getKeyword() == null){
            // 如果为空，根据另外两个属性 仓库id+分类id进行查询
            pageModel = skuRepository.findByWareIdAndCategoryId(skuEsQueryVo.getWareId(), skuEsQueryVo.getCategoryId(), pageable);
        }else{
            //如果不为空，则根据全部三个属性 仓库id+分类id+keyword进行查询（使用 仓库id+keyword 或 仓库id、+keyword 进行查询效果一样）
            pageModel = skuRepository.findByWareIdAndCategoryIdAndKeyword(skuEsQueryVo.getWareId(), skuEsQueryVo.getCategoryId(), skuEsQueryVo.getKeyword(), pageable);
        }

        //3 查询商品参加的优惠活动规则（SkuEs中有营销活动列表字段ruleList）
        //3.1 读取pageModel对象的内容，返回一个List<SkuEs>类型的对象
        List<SkuEs> skuEsList = pageModel.getContent();//getContent()读取pageModel对象的内容，返回一个List<SkuEs>类型的对象
        //3.2 遍历该List，获取每个SkuEs对象的id，存到一个skuIdList中
        List<Long> skuIdList = skuEsList.stream().map(item -> item.getId()).collect(Collectors.toList());
        //3.3 service-activity-client模块的ActivityFeignClient远程调用service-activity模块的ActivityInfoController的findActivity()方法，获取商品参加的优惠活动
        ////使返回Map<Long, List<String>>
        ////key是Long类型的skuId（因为一个商品只能参加一个活动），value是 List<String>类型的 该商品参加的优惠活动的规则列表（因为一个活动有多个规则）
        Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);

        //3.4 将活动规则封装到SkuEs的ruleList属性中
        if(skuIdToRuleListMap!=null){
            skuEsList.stream().forEach(skuEs -> {
                skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
            });
        }

        return pageModel;
    }

    /**
     * 根据skuId更新商品热度（incrHotScore）
     * @param skuId
     */
    @Override
    public void incrHotScore(Long skuId) {
        //分析：需要更新ES中sku的hotScore属性（使用redis实现）
        //一、热度:商品被用户査看了一次，更新一次es里面hotScore，es数据存到磁盘里面，每次更新进行io操作
        //二、使用Redis实现，商品每次被査看，在redis进行+1操作。约定规则，当redis这个值到达规则之后，才去更新一次es。例如设置redis中hotScore增加10次后才去es中更新hotScore，即规则:hotScore%10=0
        //三、Redis支持哪些数据类型:String、List、Set、Hash、Zset等 其中Zset是不能放重复元素，每个成员关联一个评分score，根据评分对成员进行排序

        String key = "hotScore";
        //先在redis中保存数据，每次加一
        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);
        //如果hotScore%10=0，则更新es中的hotScore
        if (hotScore % 10 == 0) {
            //从es中查询sku信息
            Optional<SkuEs> optionalSkuEs = skuRepository.findById(skuId);
            SkuEs skuEs = optionalSkuEs.get();
            //更新es中的hotScore
            skuEs.setHotScore(Math.round(hotScore));//Math类的round方法。这个方法接收一个double类型的参数，并返回最接近的长整型值
            skuRepository.save(skuEs);//save()方法，如果传入的对象中包含id，则更新该对象，否则，则保存一个新的对象到es中
        }
    }
}
