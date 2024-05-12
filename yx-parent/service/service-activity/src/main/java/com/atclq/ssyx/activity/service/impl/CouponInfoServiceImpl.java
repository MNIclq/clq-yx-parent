package com.atclq.ssyx.activity.service.impl;

import com.atclq.ssyx.activity.mapper.CouponInfoMapper;
import com.atclq.ssyx.activity.mapper.CouponRangeMapper;
import com.atclq.ssyx.activity.mapper.CouponUseMapper;
import com.atclq.ssyx.activity.service.CouponInfoService;
import com.atclq.ssyx.client.product.ProductFeignClient;
import com.atclq.ssyx.enums.CouponRangeType;
import com.atclq.ssyx.enums.CouponStatus;
import com.atclq.ssyx.model.activity.CouponInfo;
import com.atclq.ssyx.model.activity.CouponRange;
import com.atclq.ssyx.model.activity.CouponUse;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.vo.activity.CouponRuleVo;
import com.atclq.ssyx.vo.product.SkuInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponInfoMapper couponInfoMapper;

    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponUseMapper couponUseMapper;

    @Override
    public IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam) {
        QueryWrapper<CouponInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<CouponInfo> couponInfoIPage = this.page(pageParam, queryWrapper);

        // 转换优惠券类型和范围类型，这两个字段是不显示的。有对应的枚举字段，根据枚举字段来赋值
        List<CouponInfo> couponInfoList = couponInfoIPage.getRecords();
        //写法一：普通写法
//        for (CouponInfo item : couponInfoList) {
//            item.setCouponTypeString(item.getCouponType().getComment());
//            CouponRangeType rangeType = item.getRangeType();
//            if (rangeType!= null) {
//                item.setRangeTypeString(rangeType.getComment());
//            }
//        }
        //写法二：stream流 lambda表达式写法
        couponInfoList.stream().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if (rangeType!= null) {
                item.setRangeTypeString(rangeType.getComment());
            }
        });

        return couponInfoIPage;
    }

    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInFoById = this.getById(id);
        couponInFoById.setCouponTypeString(couponInFoById.getCouponType().getComment());
        CouponRangeType rangeType = couponInFoById.getRangeType();
        if(rangeType!= null){
            couponInFoById.setRangeTypeString(couponInFoById.getRangeType().getComment());
        }

        return couponInFoById;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        Map<String, Object> resultMap = new HashMap<>();
        //1 根据优惠卷id查询优惠券基本信息，即查coupon_info表
        CouponInfo couponInfo = couponInfoMapper.selectById(id);

        //2 根据优惠券id查询优惠券使用范围，即查coupon_range表中的range_id字段
        //2.1 根据优惠券id查询优惠券使用范围对象
        //法一：普通写法
//        LambdaQueryWrapper<CouponRange> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(CouponRange::getCouponId, id);
//        List<CouponRange> couponRangeList = couponRangeMapper.selectList(queryWrapper);
        //法二：stream流 lambda表达式写法
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));
        //2.2 根据优惠券使用范围对象，得到对应的range_id
        List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());

        //如果rangeType属性是SKU        range_id对应的是sku_id
        //如果rangeType属性是CATEGORY   range_id对应的是category_id

        //3 分别判断封装不同数据
        if(!CollectionUtils.isEmpty(rangeIdList)){
            //如果rangeType属性（rangeType属性是一个枚举类型属性CouponRangeType）是SKU，range_id就是sku_id，通过远程调用根据多个skuId获取对应的sku信息
            if(couponInfo.getRangeType() == CouponRangeType.SKU){
                List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
                //把数据封装到map中
                resultMap.put("skuInFoList",skuInfoList);
            }
            //如果rangeType属性（rangeType属性是一个枚举类型属性CouponRangeType）是类型CATEGORY，range_id就是categoryId，通过远程调用根据多个categoryId获取对应的分类信息
            if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                List<Category> categoryList = productFeignClient.findCategoryList(rangeIdList);
                //把数据封装到map中返回
                resultMap.put("categoryList",categoryList);
            }
        }

        return resultMap;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        //1 将之前的优惠卷规则删除
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId()));
        //2 更新优惠卷基本信息（根据couponRuleVo的couponId属性查询出CouponInfo对象，并设置CouponInfo的属性）
        CouponInfo couponInfo = couponInfoMapper.selectById(couponRuleVo.getCouponId());

        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);
        //3 保存新的优惠卷规则
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange : couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            couponRangeMapper.insert(couponRange);
        }

    }

    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {
        LambdaQueryWrapper<CouponInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CouponInfo::getCouponName, keyword);
        List<CouponInfo> couponInfoList = couponInfoMapper.selectList(queryWrapper);

        return couponInfoList;
    }

    //根据skuId+userId查询优惠卷信息
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        //1 通过service-product-feign的productFeignClient远程调用service-product的SkuInfoService的getSkuInfo()方法，根据skuId查询出对应的skuInfo信息，从而得到skuInfo的categoryId
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        Long categoryId = skuInfo.getCategoryId();
        //2 根据skuId+userId+categoryId查询出对应的CouponInfo列表
        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuId, categoryId, userId);

        return couponInfoList;
    }

    //获取购物车可以使用优惠卷列表
    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        //1 根据userId获取用户全部优惠卷
        //coupon_use  coupon_info
        List<CouponInfo> userAllCouponInfoList =
                baseMapper.selectCartCouponInfoList(userId);
        if(CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return new ArrayList<CouponInfo>();
        }

        //2 从第一步返回list集合中，获取所有优惠卷id列表
        List<Long> couponIdList = userAllCouponInfoList.stream().map(couponInfo -> couponInfo.getId())
                .collect(Collectors.toList());

        //3 查询优惠卷对应的范围  coupon_range
        //couponRangeList
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        // id in (1,2,3)
        wrapper.in(CouponRange::getCouponId,couponIdList);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);

        //4 获取优惠卷id 对应skuId列表
        //优惠卷id进行分组，得到map集合
        //     Map<Long,List<Long>>
        Map<Long,List<Long>> couponIdToSkuIdMap =
                this.findCouponIdToSkuIdMap(cartInfoList,couponRangeList);

        //5 遍历全部优惠卷集合，判断优惠卷类型
        //全场通用  sku和分类
        BigDecimal reduceAmount = new BigDecimal(0);
        CouponInfo optimalCouponInfo = null;
        for(CouponInfo couponInfo:userAllCouponInfoList) {
            //全场通用
            if(CouponRangeType.ALL == couponInfo.getRangeType()) {
                //全场通用
                //判断是否满足优惠使用门槛
                //计算购物车商品的总价
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            } else {
                //优惠卷id获取对应skuId列表
                List<Long> skuIdList
                        = couponIdToSkuIdMap.get(couponInfo.getId());
                //满足使用范围购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                        .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());
                BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            }
            if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }

        }
        //6 返回List<CouponInfo>
        if(null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }
        return userAllCouponInfoList;
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

    //获取优惠卷id 对应skuId列表
    //    //优惠卷id进行分组，得到map集合
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList,
                                                         List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();

        //couponRangeList数据处理，根据优惠卷id分组
        Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream()
                .collect(
                        Collectors.groupingBy(couponRange -> couponRange.getCouponId())
                );

        //遍历map集合
        Iterator<Map.Entry<Long, List<CouponRange>>> iterator =
                couponRangeToRangeListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();

            //创建集合 set
            Set<Long> skuIdSet = new HashSet<>();
            for (CartInfo cartInfo:cartInfoList) {
                for (CouponRange couponRange:rangeList) {
                    //判断
                    if(couponRange.getRangeType() == CouponRangeType.SKU
                            && couponRange.getRangeId().longValue() == cartInfo.getSkuId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else if(couponRange.getRangeType() == CouponRangeType.CATEGORY
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else {

                    }
                }
            }
            couponIdToSkuIdMap.put(couponId,new ArrayList<>(skuIdSet));
        }
        return couponIdToSkuIdMap;
    }


    //获取购物车对应优惠卷
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList,
                                         Long couponId) {
        //根据优惠卷id基本信息查询
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if(couponInfo == null) {
            return null;
        }
        //根据couponId查询对应CouponRange数据
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>()
                        .eq(CouponRange::getCouponId, couponId)
        );
        //对应sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        //遍历map，得到value值，封装到couponInfo对象
        List<Long> skuIdList =
                couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(skuIdList);
        return couponInfo;
    }

    //更新优惠卷使用状态
    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {
        //根据couponId查询优惠卷信息
        CouponUse couponUse = couponUseMapper.selectOne(
                new LambdaQueryWrapper<CouponUse>()
                        .eq(CouponUse::getCouponId, couponId)
                        .eq(CouponUse::getUserId, userId)
                        .eq(CouponUse::getOrderId, orderId)
        );

        //设置修改值
        couponUse.setCouponStatus(CouponStatus.USED);

        //调用方法修改
        couponUseMapper.updateById(couponUse);
    }
}
