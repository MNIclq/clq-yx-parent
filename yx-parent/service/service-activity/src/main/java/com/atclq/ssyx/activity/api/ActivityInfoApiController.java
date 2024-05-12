package com.atclq.ssyx.activity.api;

import com.atclq.ssyx.activity.service.ActivityInfoService;
import com.atclq.ssyx.activity.service.CouponInfoService;
import com.atclq.ssyx.model.activity.CouponInfo;
import com.atclq.ssyx.model.order.CartInfo;
import com.atclq.ssyx.vo.order.CartInfoVo;
import com.atclq.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "促销与优惠券接口")
@RestController
@RequestMapping("/api/activity")
@Slf4j
public class ActivityInfoApiController {
    @Resource
    private ActivityInfoService activityInfoService;

    @Resource
    private CouponInfoService couponInfoService;

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    @ApiOperation(value = "根据skuId和userId获取促销与优惠券信息")
    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") Long skuId,
                                                     @PathVariable("userId") Long userId) {
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }

    /**
     * 获取购物车满足条件的促销与优惠券信息（真TM难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难难啊！！！！！！！！！！！！！！！！！！！！！！！）
     *
     * @param cartInfoList
     * @param userId
     * @return
     */
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList, @PathVariable("userId") Long userId){
        return activityInfoService.findCartActivityAndCoupon(cartInfoList, userId);
    }

    //获取购物车每个商品参与的活动的规则，根据活动规则分组
    @PostMapping("inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList){
        return activityInfoService.findCartActivityList(cartInfoList);
    }

    //获取购物车对应优惠卷
    @PostMapping("inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,
                                         @PathVariable("couponId") Long couponId) {
        return couponInfoService.findRangeSkuIdList(cartInfoList,couponId);
    }

    //更新优惠卷使用状态
    @GetMapping("inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUseStatus(@PathVariable("couponId") Long couponId,
                                             @PathVariable("userId") Long userId,
                                             @PathVariable("orderId") Long orderId) {
        couponInfoService.updateCouponInfoUseStatus(couponId,userId,orderId);
        return true;
    }
}
