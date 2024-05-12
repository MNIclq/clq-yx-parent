package com.atclq.ssyx.acl.controller;

import com.atclq.ssyx.acl.service.PermissionService;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.acl.Permission;
import com.atclq.ssyx.model.acl.RolePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/acl/permission")
//@CrossOrigin//允许跨域请求      后面已使用网关替代
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ApiOperation("获取权限(菜单/功能)列表")
    @GetMapping
    public Result getPermissionList(){
        List<Permission> permissionList = permissionService.queryAllMenu();
        return Result.ok(permissionList);
    }

    @ApiOperation("删除一个权限(菜单/功能)项(递归删除所有子权限(菜单/功能)项)")
    @DeleteMapping("remove/{id}")
    public Result removePermission(@PathVariable Long id){
        permissionService.removeChildById(id);
        return Result.ok(null);
    }

    @ApiOperation("保存一个权限(菜单/功能)项")
    @PostMapping("save")
    public Result savePermission(@RequestBody Permission permission){
        permissionService.save(permission);
        return Result.ok(null);
    }

    @ApiOperation("更新一个权限(菜单/功能)项")
    @PutMapping("update")
    public Result updatePermission(@RequestBody Permission permission){
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    @ApiOperation("查看某个角色的权限(菜单/功能)列表(包括查询所有菜单)")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        Map<String, Object> permissionList = permissionService.getRolePermissions(roleId);
        return Result.ok(permissionList);
    }
    @ApiOperation(("给某个角色授权"))
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long roleId,
                           @RequestParam Long[] permissionIds){
        permissionService.assignRolePermissions(roleId, permissionIds);
        return Result.ok(null);
    }

}
