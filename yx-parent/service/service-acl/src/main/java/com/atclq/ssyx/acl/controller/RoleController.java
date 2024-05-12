package com.atclq.ssyx.acl.controller;

import com.atclq.ssyx.acl.service.RoleService;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.acl.Role;
import com.atclq.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags="角色管理接口")
@RestController
@RequestMapping("/admin/acl/role")
//@CrossOrigin//允许跨域请求      后面已使用网关替代
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("获取角色分页列表(带搜索-根据角色名称搜索)")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              RoleQueryVo roleQueryVo){
        Page<Role> rolePage = new Page<>(page, limit);//Page是一个泛型类，用于分页查询数据。
        IPage<Role> roleIPage = roleService.selectRolePage(rolePage, roleQueryVo);

        return Result.ok(roleIPage);
    }

    @ApiOperation("保存一个新角色")
    @PostMapping("save")
    public Result saveRole(@RequestBody Role role) {
        roleService.save(role);
        return Result.ok(null);
    }

    @ApiOperation("获取某个角色")
    @GetMapping("get/{id}")
    public Result getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    @ApiOperation("更新一个角色")
    @PutMapping("update")
    public Result updateRole(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.ok(null);
    }

    @ApiOperation("获取一个角色的所有权限列表")
    @GetMapping("toAssign/{roleId}")
    public Result getRoleAssign(@PathVariable Long roleId) {
        Map<String, Object> permissionList = roleService.getRolePermissions(roleId);
        return Result.ok(permissionList);
    }

    @ApiOperation("删除某个角色")
    @DeleteMapping("remove/{id}")
    public Result removeRoleById(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除角色信息")
    @DeleteMapping("batchRemove")
    public Result removeRolesByIds(@RequestBody List<Long> ids){
        roleService.removeByIds(ids);
        return Result.ok(null);
    }
}
