package com.atclq.ssyx.acl.service.impl;

import com.atclq.ssyx.acl.mapper.PermissionMapper;
import com.atclq.ssyx.acl.mapper.RoleMapper;
import com.atclq.ssyx.acl.service.PermissionService;
import com.atclq.ssyx.acl.service.RolePermissionService;
import com.atclq.ssyx.acl.utils.MyPermissionHelper;
import com.atclq.ssyx.model.acl.Permission;
import com.atclq.ssyx.model.acl.RolePermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;//这里可以用permissionMapper，也可以用baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了相应的mapper

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public List<Permission> queryAllMenu() {
        List<Permission> allPermissionList = baseMapper.selectList(null);//查询所有的菜单  //这里可以用permissionMapper，也可以用baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了相应的mapper

        List<Permission> result = MyPermissionHelper.buildPermission(allPermissionList);//构建树结构

        return result;
    }

    @Override
    public void removeChildById(Long id) {
        List<Long> idList = new ArrayList<>();

        this.getAllPermissionId(id,idList);

        idList.add(id);

        permissionMapper.deleteBatchIds(idList);
    }
    private void getAllPermissionId(Long id, List<Long> idList) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        List<Permission> childList = permissionMapper.selectList(wrapper);

//        childList.stream().forEach(item -> {
//            idList.add(item.getId());
//            this.getAllPermissionId(item.getId(), idList);
//        });

        for(Permission permission : childList){
            idList.add(permission.getId());
            this.getAllPermissionId(permission.getId(), idList);
        }
    }


    @Override
    public void assignRolePermissions(Long roleId, Long[] permissionIds) {
        //先删除角色已分配的权限（菜单）数据
//        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(RolePermission::getRoleId, roleId);
//        rolePermissionService.remove(wrapper);

        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));

        //给角色分配新的权限（菜单）
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for(Long permissionId : permissionIds){
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionList.add(rolePermission);
        }
        rolePermissionService.saveBatch(rolePermissionList);

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
        //通俗写法
//        List<Long> permissionIdList = new ArrayList<>();
//        for(RolePermission rolePermission : rolePermissionList){
//            permissionIdList.add(rolePermission.getPermissionId());
//        }
        //stream写法
        List<Long> permissionIdList = rolePermissionList.stream()
                .map(item -> item.getPermissionId())
                .collect(Collectors.toList());
        //使用stream()方法将rolePermissionList转换为一个Stream对象。
        //使用map()方法对Stream中的每个元素执行一个函数，这里的函数是item -> item.getPermissionId(),表示获取每个元素(rolePermissionList中的元素)的权限（菜单）ID(roleId)。
        //使用collect()方法将处理后的Stream对象转换回一个新的列表，这里使用的收集器是Collectors.toList(),表示将处理后的元素收集到一个新的List中。
        
        //遍历所有权限（菜单）列表，把已分配给角色的权限（菜单）添加到一个新的列表中
        List<Permission> assignedPermissionsList = new ArrayList<>();
        for(Permission permission : allPermissionsList) {
            if (permissionIdList.contains(permission.getId())) {
                assignedPermissionsList.add(permission);
            }
        }

        //封装结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("allPermissions", allPermissionsList);
        result.put("assignPermissions", assignedPermissionsList);

        return result;
    }

}
