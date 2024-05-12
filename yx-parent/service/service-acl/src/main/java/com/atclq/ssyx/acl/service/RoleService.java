package com.atclq.ssyx.acl.service;

import com.atclq.ssyx.model.acl.Role;
import com.atclq.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;


public interface RoleService extends IService<Role> {

    IPage<Role> selectRolePage(Page<Role> rolePage, RoleQueryVo roleQueryVo);

    void assignAdminRoles(Long adminId, Long[] roleIds);

    Map<String, Object> getAdminRoles(Long adminId);

    Map<String, Object> getRolePermissions(Long roleId);

}
