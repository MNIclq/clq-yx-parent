package com.atclq.ssyx.product.mapper;


import com.atclq.ssyx.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Repository
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    //解锁库存
    void unlockStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);

    //验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);

    //锁定库存
    Integer lockStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
}
