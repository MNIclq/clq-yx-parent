package com.atclq.ssyx.user.service;

import com.atclq.ssyx.model.user.User;
import com.atclq.ssyx.vo.user.LeaderAddressVo;
import com.atclq.ssyx.vo.user.UserLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author atclq
 * @since 2024-05-02
 */
public interface UserService extends IService<User> {

    User getUserByOpenId(String openId);

    LeaderAddressVo getLeaderAddressVoByUserId(Long userId);

    UserLoginVo getUserLoginVo(Long userId);
}
