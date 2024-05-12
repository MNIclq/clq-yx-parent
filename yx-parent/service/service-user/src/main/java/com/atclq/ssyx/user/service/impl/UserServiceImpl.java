package com.atclq.ssyx.user.service.impl;

import com.atclq.ssyx.enums.UserType;
import com.atclq.ssyx.model.user.Leader;
import com.atclq.ssyx.model.user.User;
import com.atclq.ssyx.model.user.UserDelivery;
import com.atclq.ssyx.user.mapper.LeaderMapper;
import com.atclq.ssyx.user.mapper.UserDeliveryMapper;
import com.atclq.ssyx.user.mapper.UserMapper;
import com.atclq.ssyx.user.service.UserService;
import com.atclq.ssyx.vo.user.LeaderAddressVo;
import com.atclq.ssyx.vo.user.UserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-05-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private LeaderMapper leaderMapper;

    @Override
    public User getUserByOpenId(String openId) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId));
        return user;
    }

    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long userId) {
        //根据userId和isDefault=1查询出UserDelivery对象
        LambdaQueryWrapper<UserDelivery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDelivery::getUserId, userId);
        queryWrapper.eq(UserDelivery::getIsDefault, 1);

        UserDelivery userDelivery = userDeliveryMapper.selectOne(queryWrapper);
        if(null == userDelivery) return null;

        //根据UserDelivery对象中的leaderId查询出对应的Leader对象
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());

        //用LeaderAddressVo对象封装需要查询的Leader对象属性值和UserDelivery对象中的仓库id属性值
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);//使用Apache Commons BeanUtils库中的copyProperties方法将leader对象的属性值复制到leaderAddressVo对象中（复制的是属性名一致的属性）
        //设置leader对象和leaderAddressVo对象中属性名不一致的属性值，以及userDelivery对象中的仓库id属性值
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getUserLoginVo(Long userId) {
        UserLoginVo userLoginVo = new UserLoginVo();

        User user = this.getById(userId);
        BeanUtils.copyProperties(user, userLoginVo);
        userLoginVo.setUserId(userId);
//        userLoginVo.setNickName(user.getNickName());
//        userLoginVo.setPhotoUrl(user.getPhotoUrl());
//        userLoginVo.setOpenId(user.getOpenId());
//        userLoginVo.setIsNew(user.getIsNew());

        //如果是团长获取当前团长id与对应的仓库id
//        if(user.getUserType() == UserType.LEADER) {
//            LambdaQueryWrapper<Leader> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(Leader::getUserId, userId);
//            queryWrapper.eq(Leader::getCheckStatus, 1);
//            Leader leader = leaderMapper.selectOne(queryWrapper);
//            if(null != leader) {
//                userLoginVo.setLeaderId(leader.getId());
//                Long wareId = regionFeignClient.getWareId(leader.getRegionId());
//                userLoginVo.setWareId(wareId);
//            }
//        } else {
            //如果是会员获取当前会员对应的仓库id
            LambdaQueryWrapper<UserDelivery> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserDelivery::getUserId, userId);
            queryWrapper.eq(UserDelivery::getIsDefault, 1);
            UserDelivery userDelivery = userDeliveryMapper.selectOne(queryWrapper);
            if(null != userDelivery) {
                userLoginVo.setLeaderId(userDelivery.getLeaderId());
                userLoginVo.setWareId(userDelivery.getWareId());
            } else {
                userLoginVo.setLeaderId(1L);
                userLoginVo.setWareId(1L);
            }
//        }
        return userLoginVo;
    }
}
