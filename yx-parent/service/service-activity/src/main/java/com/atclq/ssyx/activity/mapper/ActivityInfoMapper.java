package com.atclq.ssyx.activity.mapper;

import com.atclq.ssyx.model.activity.ActivityInfo;
import com.atclq.ssyx.model.activity.ActivityRule;
import com.atclq.ssyx.model.activity.ActivitySku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author atclq
 * @since 2024-04-28
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectExistSkuIdList(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> findActivityRule(@Param("skuId") Long skuId);

    //根据所有skuId列表获取参与活动
    List<ActivitySku> selectCartActivity(@Param("skuIdList") List<Long> skuIdList);
}
