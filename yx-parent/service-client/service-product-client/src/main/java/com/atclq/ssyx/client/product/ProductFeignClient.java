package com.atclq.ssyx.client.product;
//供客户端通过OpenFeign进行远程调用的方法
import com.atclq.ssyx.model.product.Category;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.vo.product.SkuInfoVo;
import com.atclq.ssyx.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-product")
public interface ProductFeignClient {

    /**
     * 根据分类id获取商品分类信息
     * @param categoryId
     * @return
     */
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    /**
     * 根据skuId获取商品sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId列表批量获取sku信息
     * @param skuIdList
     * @return
     */
    @PostMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList);

    /**
     * 根据关键字获取sku列表
     * @param keyword
     * @return
     */
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword);

    /**
     * 批量获取分类信息
     * @param categoryIdList
     * @return
     */
    @PostMapping("/api/product/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList);

    /**
     * 获取全部分类信息
     * @return
     */
    @GetMapping("/api/product/inner/findAllCategoryList")
    List<Category> findAllCategoryList();

    /**
     * 获取新人专享商品列表
     * @return
     */
    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    List<SkuInfo> findNewPersonSkuInfoList();


    /**
     * 根据skuId获取sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId);

    //验证和锁定库存
    @ApiOperation(value = "锁定库存")
    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable("orderNo") String orderNo);

}
