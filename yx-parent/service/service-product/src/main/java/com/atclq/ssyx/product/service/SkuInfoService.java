package com.atclq.ssyx.product.service;


import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.vo.product.SkuInfoQueryVo;
import com.atclq.ssyx.vo.product.SkuInfoVo;
import com.atclq.ssyx.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo);

    void saveSkuInfo(SkuInfoVo skuInfoVo);

    SkuInfoVo getSkuInfo(Long id);

    void updateSkuInfo(SkuInfoVo skuInfoVo);

    void check(Long skuId, Integer status);

    void publish(Long skuId, Integer status);

    void isNewPerson(Long skuId, Integer status);

    List<SkuInfo> findSkuInfoList(List<Long> skuIdList);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findNewPersonList();

    SkuInfoVo getSkuInfoVo(Long skuId);

    //验证和锁定库存
    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);
}
