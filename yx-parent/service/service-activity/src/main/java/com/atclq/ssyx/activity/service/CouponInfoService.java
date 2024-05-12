package com.atclq.ssyx.activity.service;

import com.atclq.ssyx.model.activity.CouponInfo;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
public interface CouponInfoService extends IService<CouponInfo> {

    IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam);

    CouponInfo getCouponInfo(Long id);

    Map<String, Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    List<CouponInfo> findCouponByKeyword(String keyword);

    //根据skuId+userId查询优惠卷信息
    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    //获取购物车可以使用优惠卷列表
    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);

    //获取购物车对应优惠卷
    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    //更新优惠卷使用状态
    void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}
