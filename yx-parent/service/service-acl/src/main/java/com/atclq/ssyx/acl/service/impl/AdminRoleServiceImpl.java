package com.atclq.ssyx.acl.service.impl;

import com.atclq.ssyx.acl.mapper.AdminRoleMapper;
import com.atclq.ssyx.acl.service.AdminRoleService;
import com.atclq.ssyx.model.acl.AdminRole;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
