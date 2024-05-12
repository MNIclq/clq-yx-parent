package com.atclq.ssyx.acl.service.impl;

import com.atclq.ssyx.acl.mapper.RolePermissionMapper;
import com.atclq.ssyx.acl.service.RolePermissionService;
import com.atclq.ssyx.model.acl.RolePermission;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
