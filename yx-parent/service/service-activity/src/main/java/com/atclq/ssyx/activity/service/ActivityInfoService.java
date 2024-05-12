package com.atclq.ssyx.activity.service;

import com.atclq.ssyx.model.activity.ActivityInfo;
import com.atclq.ssyx.model.activity.ActivityRule;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.vo.activity.ActivityRuleVo;
import com.atclq.ssyx.vo.order.CartInfoVo;
import com.atclq.ssyx.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam);

    //根据活动id获取活动规则
    Map<String, Object> findActivityRuleList(Long activityId);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    //根据skuId和userId获取促销与优惠券信息
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    //根据skuId获取活动规则数据
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);

    //获取购物车满足条件的促销与优惠券信息
    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    //获取购物车每个商品参与的活动的规则，根据活动规则分组
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
}
