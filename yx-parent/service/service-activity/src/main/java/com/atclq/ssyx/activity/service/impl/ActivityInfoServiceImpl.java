package com.atclq.ssyx.activity.service.impl;

import com.atclq.ssyx.activity.mapper.ActivityInfoMapper;
import com.atclq.ssyx.activity.mapper.ActivityRuleMapper;
import com.atclq.ssyx.activity.mapper.ActivitySkuMapper;
import com.atclq.ssyx.activity.service.ActivityInfoService;
import com.atclq.ssyx.activity.service.CouponInfoService;
import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.enums.ActivityType;
import com.atclq.ssyx.model.activity.ActivityInfo;
import com.atclq.ssyx.model.activity.ActivityRule;
import com.atclq.ssyx.model.activity.ActivitySku;
import com.atclq.ssyx.model.activity.CouponInfo;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.vo.activity.ActivityRuleVo;
import com.atclq.ssyx.vo.order.CartInfoVo;
import com.atclq.ssyx.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
@Service
@Slf4j
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityInfoMapper activityInfoMapper;

    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;


    @Autowired
    private CouponInfoService couponInfoService;

    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        QueryWrapper<ActivityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ActivityInfo> Ipage = activityInfoMapper.selectPage(pageParam, queryWrapper);

        //设置活动类型名称 ActivityInfo实体类中不显示的activityTypeString属性的值是通过显示的枚举类属性activityTyped的comment属性获取的
        //写法一 ；普通遍历赋值
//        for (ActivityInfo activityInfo : Ipage.getRecords()) {
//            activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
//        }
        //写法二 ；stream流式处理
//        Ipage.getRecords().stream().forEach(item -> item.setActivityTypeString(item.getActivityType().getComment()));
        List<ActivityInfo> ActivityInfoList = Ipage.getRecords();//getRecords()方法，返回当前页的记录列表。
        ActivityInfoList.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });
        //包含花括号的 lambda 表达式，它允许在其中包含多条语句。
        //在第一行代码中，lambda 表达式内部只包含了一条语句，因此可以省略花括号。而在第二行代码中，lambda 表达式内部包含了多条语句，使用花括号来定义代码块，以便在单个 lambda 表达式中执行多个操作。
        //在功能上，这两行代码执行的操作是一样的，只不过第二行代码使用了花括号来明确指定了代码块的范围。

        return Ipage;
    }

    @Override
    public Map<String,Object> findActivityRuleList(Long id) {
        Map<String,Object> resultMap = new HashMap<>();
        //1 根据活动id查询活动规则列表，即activity_rule表，得到活动规则ActivityRule列表
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, id));

        //1.1 如果活动规则列表为空，则直接返回空Map列表
        if(activityRuleList.isEmpty()) {
            return resultMap;
        }

        //2 根据活动id查询使用这个活动规则的商品列表，即activity_sku表，得到ActivitySku列表
        LambdaQueryWrapper<ActivitySku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySku::getActivityId, id);
        List<ActivitySku> activitySkuList = activitySkuMapper.selectList(wrapper);

        //2.1 根据sku列表获取skuId列表
        List<Long> skuIdList = activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());

        //2.2 通过service-client模块的ProductFeignClient远程调用service-product模块的接口，根据skuId列表去service-product模块的product表中查询商品列表，得到商品信息SkuInfo列表
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);

        //3 分别将活动规则列表和商品信息skuInfo列表封装到map中返回
        resultMap.put("activityRuleList", activityRuleList);
        resultMap.put("skuInfoList", skuInfoList);

        return resultMap;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        //1 从传过来的activityRuleVo中获取活动id即activityId，根据这个活动id删除原有的活动规则 需要删除两个相关表的相关记录，一个是activity_rule表，一个是activity_sku表
        Long activityId = activityRuleVo.getActivityId();
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        activitySkuMapper.delete(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId));

        //2 重新添加新的活动规则
        //2.1 从传过来的activityRuleVo中获取活动规则列表数据，给所有规则设置活动id即activityId和活动类型即activityInfo中的activityType属性（activityInfo通过activityId查询得到）
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        for (ActivityRule activityRule : activityRuleList) {
            activityRule.setActivityId(activityId);
            activityRule.setActivityType(activityInfo.getActivityType());

            activityRuleMapper.insert(activityRule);
        }

        //2.2 从传过来的activityRuleVo中获取活动规则范围（即活动包含的sku列表）数据，给所有sku设置活动id即activityId
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku : activitySkuList) {
            activitySku.setActivityId(activityId);

            activitySkuMapper.insert(activitySku);
        }

    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        //1 根据传入的关键字查询商品信息，通过service-client模块的ProductFeignClient远程调用service-product模块的接口，根据商品名称模糊查询商品列表，得到商品信息SkuInfo列表
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoByKeyword(keyword);

        //如果查询不到商品信息，则直接返回空List列表
        if(skuInfoList.isEmpty()) {
            return null;
        }

        //2 遍历商品信息SkuInfo列表，获取所有商品的id
        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());

        //3 判断商品是否已经参加过活动，如果已经参加过活动，且活动正在进行中，则不能再次参加另外的活动
        //3.1 需要通过查询activity_sku表和activity_info表来判断商品是否已经参加过活动
        List<Long> existSkuIdList = baseMapper.selectExistSkuIdList(skuIdList);//selectExistSkuIdList(skuIdList)方法在mapper层的接口中声明，并在mapper层的xml文件中实现

        //3.2 过滤掉已经参加过活动的商品
        List<SkuInfo> notExistSkuInfoList = new ArrayList<>();
        for(SkuInfo skuInfo : skuInfoList) {
            if(!existSkuIdList.contains(skuInfo.getId())) {
                notExistSkuInfoList.add(skuInfo);
            }
        }
        return notExistSkuInfoList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> resultMap = new HashMap<>();
        //1 遍历skuIdList，查询每个skuId对应的活动信息，即activity_info表，得到活动信息ActivityInfo列表
        skuIdList.forEach(skuId -> {
            List<ActivityRule> activityRuleList = activityInfoMapper.findActivityRule(skuId);
            //2 如果活动信息列表不为空，封装结果到resultMap中
            if(!CollectionUtils.isEmpty(activityRuleList)){
                List<String> ruleList = new ArrayList<>();
                //处理规则名称
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                resultMap.put(skuId, ruleList);
            }
        });
        return resultMap;
    }
    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }

    /**
     * 根据skuId和userId获取促销与优惠券信息
     * @param skuId
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        Map<String, Object> resultMap = new HashMap<>();

        //1 根据skuId查询活动信息，一个活动有多个规则（调用的这个方法就在这个service层的实现类中）
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        //2 根据skuId+userId查询优惠卷信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        //3 封装结果到map中并返回
        resultMap.put("activityRuleList",activityRuleList);
        resultMap.put("couponInfoList", couponInfoList);

        return resultMap;
    }

    /**
     * 根据skuId获取活动规则数据
     * @param skuId
     * @return
     */
    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        //构造优惠规则名称
        for (ActivityRule activityRule : activityRuleList) {
            activityRule.setRuleDesc(this.getRuleDesc(activityRule));
        }

        return activityRuleList;
    }

    /**
     * 获取购物车满足条件的促销与优惠券信息
     * @param cartInfoList
     * @param userId
     * @return
     */
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        //1 获取购物车每个商品参与的活动的规则，根据活动规则分组
        //一个规则对应多个商品 CartInfoVo
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        //2 计算参与活动之后金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //3 获取购物车可以使用优惠卷列表
        List<CouponInfo> couponInfoList =
                couponInfoService.findCartCouponInfo(cartInfoList,userId);

        //4 计算商品使用优惠卷之后金额，一次只能使用一张优惠卷
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1)
                    .map(couponInfo -> couponInfo.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        //5 计算没有参与活动，没有使用优惠卷原始金额
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //6 最终金额
        BigDecimal totalAmount =
                originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        //7 封装需要数据到OrderConfirmVo,返回
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    /**
     * 获取购物车每个商品参与的活动的规则，根据活动规则分组
     * @param cartInfoList
     * @return
     */
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        //创建最终返回集合
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        //获取所有skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        //根据所有skuId列表获取参与活动 ActivitySku
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);
        //根据活动进行分组，每个活动里面有哪些skuId信息
        //map里面key是分组字段 活动id
        // value是每组里面sku列表数据，set集合
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream()
                .collect(
                        Collectors.groupingBy(
                                ActivitySku::getActivityId,
                                Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())
                        )//使用groupingBy收集器对Stream中的元素进行分组。
                        // 分组的依据是ActivitySku对象的activityId属性，分组后的元素会被放入一个新的Map中。
                        // 同时，使用mapping收集器将每个ActivitySku对象映射为其对应的SkuId,并将这些SkuId放入一个Set中。
                        // 最后，将这个Set作为新Map中对应activityId的值。
                );

        //获取活动里面规则数据
        //key是活动id  value是活动里面规则列表数据
        Map<Long,List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();
        //所有活动id
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId)
                .collect(Collectors.toSet());
        if(!CollectionUtils.isEmpty(activityIdSet)) {
            //activity_rule表
            LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(ActivityRule::getConditionAmount,ActivityRule::getConditionNum);
            wrapper.in(ActivityRule::getActivityId,activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapper);

            //封装到activityIdToActivityRuleListMap里面
            //根据活动id进行分组
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(
                    Collectors.groupingBy(activityRule -> activityRule.getActivityId())
            );
        }

        Set<Long> activitySkuIdSet = new HashSet<>();
        //有活动的购物项skuId
        if(!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            //遍历activityIdToSkuIdListMap集合  迭代器
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                //活动id
                Long activityId = entry.getKey();
                //每个活动对应skuId列表
                Set<Long> currentActivitySkuIdSet = entry.getValue();
                //获取当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream()
                        .filter(cartInfo ->
                                currentActivitySkuIdSet.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                //计数购物项总金额和总数量
                BigDecimal activityTotalAmount =
                        this.computeTotalAmount(currentActivityCartInfoList);
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);

                //计算活动对应规则
                //根据activityId获取活动对应规则
                List<ActivityRule> currentActivityRuleList =
                        activityIdToActivityRuleListMap.get(activityId);
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                //判断活动类型：满减和打折
                ActivityRule activityRule = null;
                if(activityType == ActivityType.FULL_REDUCTION) {//满减"
                    activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                } else {//满量
                    activityRule = this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                }

                //CartInfoVo封装
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);

                //记录哪些购物项参与活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        //没有活动的购物项skuId
        //获取哪些skuId没有参加活动
        skuIdList.removeAll(activitySkuIdSet);
        if(!CollectionUtils.isEmpty(skuIdList)) {
            //skuId对应购物项
            Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream().collect(
                    Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo)
            );
            for(Long skuId  : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);//没有活动

                List<CartInfo> cartInfos = new ArrayList<>();
                cartInfos.add(skuIdCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(cartInfos);

                cartInfoVoList.add(cartInfoVo);
            }
        }

        return cartInfoVoList;
    }

    /**
     * 计算满量打折最优规则
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }


}
