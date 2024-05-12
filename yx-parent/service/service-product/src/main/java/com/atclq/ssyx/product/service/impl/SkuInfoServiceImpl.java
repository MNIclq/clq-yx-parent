package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.common.constant.RedisConst;
import com.atclq.ssyx.common.exception.SsyxException;
import com.atclq.ssyx.common.result.ResultCodeEnum;
import com.atclq.ssyx.model.product.SkuAttrValue;
import com.atclq.ssyx.model.product.SkuImage;
import com.atclq.ssyx.model.product.SkuInfo;
import com.atclq.ssyx.model.product.SkuPoster;
import com.atclq.ssyx.mq.service.RabbitService;
import com.atclq.ssyx.mq.service.constant.MqConst;
import com.atclq.ssyx.product.mapper.SkuInfoMapper;
import com.atclq.ssyx.product.service.SkuAttrValueService;
import com.atclq.ssyx.product.service.SkuImageService;
import com.atclq.ssyx.product.service.SkuInfoService;
import com.atclq.ssyx.product.service.SkuPosterService;
import com.atclq.ssyx.vo.product.SkuInfoQueryVo;
import com.atclq.ssyx.vo.product.SkuInfoVo;
import com.atclq.ssyx.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
@Slf4j
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private SkuImageService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private RabbitService rabbitService;//用于商品上架下架发送mq消息更新es数据

    @Autowired
    private RedisTemplate redisTemplate;
    private RedissonClient redissonClient;

    @Override
    public IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        //获取条件值
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        //封装条件
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)) {
            wrapper.like(SkuInfo::getSkuName,keyword);
        }
        if(!StringUtils.isEmpty(skuType)) {
            wrapper.eq(SkuInfo::getSkuType,skuType);
        }
        if(!StringUtils.isEmpty(categoryId)) {
            wrapper.eq(SkuInfo::getCategoryId,categoryId);
        }
        //调用方法查询
        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(pageParam, wrapper);
        return skuInfoPage;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        //保存sku信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        skuInfoMapper.insert(skuInfo);

        //保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        int sort1 = 1;
        if(!CollectionUtils.isEmpty(skuPosterList)){
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
                sort1++;
            }
            skuPosterService.saveBatch(skuPosterList);
        }

        //保存sku图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        int sort2 = 1;
        if(!CollectionUtils.isEmpty(skuImagesList)){
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImage.setSort(sort2);
                sort2++;
            }
            skuImagesService.saveBatch(skuImagesList);
        }

        //保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        int sort3 = 1;
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValue.setSort(sort3);
                sort3++;
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }

    }

    @Override
    public SkuInfoVo getSkuInfo(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //TODO skuImagesService  skuPosterService  skuAttrValueService分别添加方法
        List<SkuImage> skuImageList = skuImagesService.findBySkuId(skuId);
        List<SkuPoster> skuPosterList = skuPosterService.findBySkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.findBySkuId(skuId);

        BeanUtils.copyProperties(skuInfoVo, skuInfo);

        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);

        return skuInfoVo;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        //更新sku信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        skuInfoMapper.insert(skuInfo);

        Long skuId = skuInfoVo.getId();

        //删除sku海报
        skuPosterService.remove(new LambdaQueryWrapper<SkuPoster>().eq(SkuPoster::getSkuId, skuId));
        //保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if(!CollectionUtils.isEmpty(skuPosterList)){
            int sort = 1;
            for(SkuPoster skuPoster:skuPosterList){
                skuPoster.setSkuId(skuId);
                sort++;
            }
            skuPosterService.saveBatch(skuPosterList);
        }

        //删除sku图片
        skuImagesService.remove(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
        //保存sku图片
        List<SkuImage> skuImageList = skuInfoVo.getSkuImagesList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            int sort = 1;
            for(SkuImage skuImage:skuImageList){
                skuImage.setSkuId(skuId);
                skuImage.setSort(sort);
                sort++;
            }
            skuImagesService.saveBatch(skuImageList);
        }

        //删除sku平台属性
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId, skuId));
        //保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            int sort = 1;
            for(SkuAttrValue skuAttrValue:skuAttrValueList){
                skuAttrValue.setSkuId(skuId);
                skuAttrValue.setSort(sort);
                sort++;
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void check(Long skuId, Integer status) {
        // 更改发布状态
        SkuInfo skuInfoUp = skuInfoMapper.selectById(skuId);
        skuInfoUp.setCheckStatus(status);
        skuInfoMapper.updateById(skuInfoUp);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void publish(Long skuId, Integer status) {
        // 更改发布状态
        if(status == 1) {
            SkuInfo skuInfoUp = skuInfoMapper.selectById(skuId);
            skuInfoUp.setPublishStatus(1);
            skuInfoMapper.updateById(skuInfoUp);
            //发送mq消息更新es数据 整合mq把数据同步到es里面
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                                      MqConst.ROUTING_GOODS_UPPER,
                                      skuId);
        } else {
            SkuInfo skuInfoUp = skuInfoMapper.selectById(skuId);
            skuInfoUp.setPublishStatus(0);
            skuInfoMapper.updateById(skuInfoUp);
            //发送mq消息更新es数据 整合mq把数据同步到es里面
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                                      MqConst.ROUTING_GOODS_LOWER,
                                      skuId);
        }
    }

    @Override
    public void isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfoUp = skuInfoMapper.selectById(skuId);
        skuInfoUp.setIsNewPerson(status);
        skuInfoMapper.updateById(skuInfoUp);
    }


    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIdList) {
//        List<SkuInfo> skuInfoList = skuInfoMapper.selectBatchIds(skuIdList);//通过mapper层调用BaseMapper的selectBatchIds方法
        List<SkuInfo> skuInfoList = this.listByIds(skuIdList);//"this" 关键字代表当前类的实例，可以直接调用service层IService的listByIds方法

        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SkuInfo::getSkuName, keyword);

        List<SkuInfo> skuInfoList = baseMapper.selectList(wrapper);

        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findNewPersonList() {
        //条件一：sku_info表中is_new_person=1的商品
        //条件二：sku_info表中publish_status=1的商品
        //条件三：选择符合条件一和条件二的商品按照库存量stock字段进行排序后的前三个显示
        //前三个显示方式一：wapper.last("limit 3")
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfo::getIsNewPerson, 1);
        wrapper.eq(SkuInfo::getPublishStatus, 1);
        wrapper.orderByDesc(SkuInfo::getStock);
        wrapper.last("limit 3");

        List<SkuInfo> skuInfoList = baseMapper.selectList(wrapper);

        //前三个显示方式二：分页查询
//        Page<SkuInfo> page = new Page<>(1, 3);
//
//        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SkuInfo::getIsNewPerson, 1);
//        wrapper.eq(SkuInfo::getPublishStatus, 1);
//        wrapper.orderByDesc(SkuInfo::getStock);
//
//        IPage<SkuInfo> pageInfo = baseMapper.selectPage(page, wrapper);
//        List<SkuInfo> skuInfoList = pageInfo.getRecords();


        return skuInfoList;
    }

    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        //根据skuId查询skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        //根据skuId查询sku图片
        List<SkuImage> skuImageList = skuImagesService.findBySkuId(skuId);

        //根据skuId查询sku海报
        List<SkuPoster> skuPosterList = skuPosterService.findBySkuId(skuId);

        //根据skuId查询sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.findBySkuId(skuId);

        //封装数据
        BeanUtils.copyProperties(skuInfo, skuInfoVo);//skuInfoVo继承了skuInfo，所以可以直接使用BeanUtils.copyProperties方法进行属性的复制
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);

        return skuInfoVo;
    }

    //验证和锁定库存
    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList,
                                String orderNo) {
        //1 判断skuStockLockVoList集合是否为空
        if(CollectionUtils.isEmpty(skuStockLockVoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        //2 遍历skuStockLockVoList得到每个商品，验证库存并锁定库存，具备原子性
        skuStockLockVoList.stream().forEach(skuStockLockVo -> {
            this.checkLock(skuStockLockVo);
        });

        //3 只要有一个商品锁定失败，所有锁定成功的商品都解锁
        boolean flag = skuStockLockVoList.stream()
                .anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if(flag) {
            //所有锁定成功的商品都解锁
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock)
                    .forEach(skuStockLockVo -> {
                        baseMapper.unlockStock(skuStockLockVo.getSkuId(),
                                skuStockLockVo.getSkuNum());
                    });
            //返回失败的状态
            return false;
        }

        //4 如果所有商品都锁定成功了，redis缓存相关数据，为了方便后面解锁和减库存
        redisTemplate.opsForValue()
                .set(RedisConst.SROCK_INFO+orderNo,skuStockLockVoList);
        return true;
    }
    // 遍历skuStockLockVoList得到每个商品，验证库存并锁定库存，具备原子性
    private void checkLock(SkuStockLockVo skuStockLockVo) {
        //获取锁
        //公平锁
        RLock rLock =
                this.redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        //加锁
        rLock.lock();

        try {
            //验证库存
            SkuInfo skuInfo =
                    baseMapper.checkStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            //判断没有满足条件商品，设置isLock值false，返回
            if(skuInfo == null) {
                skuStockLockVo.setIsLock(false);
                return;
            }
            //有满足条件商品
            //锁定库存:update
            Integer rows =
                    baseMapper.lockStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            if(rows == 1) {
                skuStockLockVo.setIsLock(true);
            }
        } finally {
            //解锁
            rLock.unlock();
        }
    }

}
