package com.atclq.ssyx.acl.service.impl;

import com.atclq.ssyx.acl.mapper.AdminRoleMapper;
import com.atclq.ssyx.acl.mapper.PermissionMapper;
import com.atclq.ssyx.acl.mapper.RoleMapper;
import com.atclq.ssyx.acl.service.AdminRoleService;
import com.atclq.ssyx.acl.service.RolePermissionService;
import com.atclq.ssyx.acl.service.RoleService;
import com.atclq.ssyx.model.acl.*;
import com.atclq.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService{

    @Autowired
    private RoleMapper roleMapper;//这里可以用roleMapper，也可以用baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了相应的mapper

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private PermissionMapper permissionMapper;//这里可以用permissionMapper，也可以用baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了相应的mapper

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public IPage<Role> selectRolePage(Page<Role> rolePage, RoleQueryVo roleQueryVo) {
        //获取条件值：角色名称
        String roleName = roleQueryVo.getRoleName();
        //创建条件构造器对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        //判断条件值是否为空     不为空，则封装条件
        if(roleName!= null && !roleName.equals(" ")){
            wrapper.like(Role::getRoleName,roleName);//模糊查询
        }
        //使用BaseMapper的selectPage方法完成条件分页查询
//        IPage<Role> roleIPage = baseMapper.selectPage(rolePage, wrapper);
        IPage<Role> roleIPage = roleMapper.selectPage(rolePage, wrapper);//这里用roleMapper和baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了roleMapper。

        return roleIPage;
    }

    @Override
    public Map<String, Object> getAdminRoles(Long adminId) {
        //1从角色表中查询所有角色
        List<Role> allRolesList= baseMapper.selectList(null);

        //2根据用户id查询用户所分配的角色     查出来的是AdminRole对象列表，里面包含角色id和用户id
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);

        //获取用户已分配的角色的id列表
        //通俗写法
//        List<Long> roleIdList = new ArrayList<>();
//        for (AdminRole adminRole : adminRoleList) {
//            roleIdList.add(adminRole.getRoleId());
//        }
        //stream写法
        List<Long> roleIdList = adminRoleList.stream()
                .map(item -> item.getRoleId())
                .collect(Collectors.toList());
        //使用stream()方法将adminRoleList转换为一个Stream对象。
        //使用map()方法对Stream中的每个元素执行一个函数，这里的函数是item -> item.getRoleId(),表示获取每个元素(adminRoleList中的元素)的角色ID(roleId)。
        //使用collect()方法将处理后的Stream对象转换回一个新的列表，这里使用的收集器是Collectors.toList(),表示将处理后的元素收集到一个新的List中。

        //遍历所有角色列表，把已分配给用户的角色添加到一个新的列表中
        List<Role> assignedRoleList = new ArrayList<>();
        for (Role role : allRolesList) {
            if (roleIdList.contains(role.getId())) {
                assignedRoleList.add(role);
            }
        }

        //封装结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("allRolesList", allRolesList);
        result.put("assignRoles", assignedRoleList);

        return result;
    }

    @Override
    public void assignAdminRoles(Long adminId, Long[] roleIds) {
        //先删除用户已分配的角色数据
//        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(AdminRole::getAdminId, adminId);
//        adminRoleService.remove(wrapper);

        adminRoleService.remove(new QueryWrapper<AdminRole>().eq("admin_id", adminId));

//        for(Long roleId : roleIds){
//            AdminRole adminRole = new AdminRole();
//            adminRole.setAdminId(adminId);
//            adminRole.setRoleId(roleId);
//            adminRoleService.save(adminRole);
//        }

        //给用户分配新的角色
        List<AdminRole> adminRoleList = new ArrayList<>();
        for(Long roleId : roleIds){
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            adminRoleList.add(adminRole);
        }
        adminRoleService.saveBatch(adminRoleList);
    }

    @Override
    public Map<String, Object> getRolePermissions(Long roleId) {
        //从权限（菜单）表中获取所有权限（菜单）
        List<Permission> allPermissionsList = permissionMapper.selectList(null);
        //根据角色id查询角色已拥有的权限（菜单）  查出来的是RolePermission对象列表，其中包含permissionId和roleId属性
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);
        //获取角色已分配的权限（菜单）的id列表
        List<Long> permissionIdList = rolePermissionList.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        //遍历所有权限（菜单）列表，把已分配给角色的权限（菜单）添加到一个新的列表中
        List<Permission> assignedPermissionList = new ArrayList<>();
        for(Permission permission : allPermissionsList){
            if(permissionIdList.contains(permission.getPid())){
                assignedPermissionList.add(permission);
            }
        }
        //封装结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("allPermissions", allPermissionsList);
        result.put("assignPermissions", assignedPermissionList);

        return result;
    }

}
