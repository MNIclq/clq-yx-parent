package com.atclq.ssyx.acl.service;

import com.atclq.ssyx.model.acl.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {

    List<Permission> queryAllMenu();

    void removeChildById(Long id);

    void assignRolePermissions(Long roleId, Long[] permissionIds);

    Map<String, Object> getRolePermissions(Long roleId);
}
