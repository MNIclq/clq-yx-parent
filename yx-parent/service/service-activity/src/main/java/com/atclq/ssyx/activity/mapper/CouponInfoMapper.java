package com.atclq.ssyx.activity.mapper;

import com.atclq.ssyx.model.activity.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    //根据skuId+分类id+userId查询优惠卷信息
    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId,
                                          @Param("categoryId") Long categoryId,
                                          @Param("userId") Long userId);

    //根据userId获取用户全部优惠卷
    List<CouponInfo> selectCartCouponInfoList(Long userId);
}
