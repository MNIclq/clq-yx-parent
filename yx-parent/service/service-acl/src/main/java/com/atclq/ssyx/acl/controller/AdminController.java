package com.atclq.ssyx.acl.controller;

import com.atclq.ssyx.acl.service.AdminService;
import com.atclq.ssyx.acl.service.RoleService;
import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.common.utils.MD5;
import com.atclq.ssyx.model.acl.Admin;
import com.atclq.ssyx.vo.acl.AdminLoginVo;
import com.atclq.ssyx.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/acl/user")
//@CrossOrigin // 解决跨域问题            后面已使用网关替代
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

//    @ApiOperation("登录")
//    @PostMapping("/login")
//    public Result login(@RequestBody String username,
//                        @RequestBody String password,
//                        AdminLoginVo adminLoginVo){
//        adminService.adminLogin(username, password, adminLoginVo);
//        return Result.ok(null);
//    }

//    @ApiOperation("获取用户信息(根据token)")
//    @GetMapping("/info")
//    public Result getAdminInfo(@RequestHeader("Authorization") String token){
//        adminService.getUserInfo(token);
//        return Result.ok(null);
//    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public Result Logout(){
        return Result.ok(null);
    }

//    @ApiOperation("获取当前用户的菜单权限列表")
//    @GetMapping("/menu")
//    public Result getAdminMenu(){
//
//    }

    @ApiOperation("获取后台用户分页列表(带搜索)")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              AdminQueryVo adminQueryVo){
        Page<Admin> adminPage = new Page<>(page, limit);
        IPage<Admin> adminIPage = adminService.selectAdminPage(adminPage, adminQueryVo);

        return Result.ok(adminIPage);
    }

    @ApiOperation("根据ID获取某个后台用户")
    @GetMapping("/get/{id}")
    public Result getAdminById(@PathVariable Long id){
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }

    @ApiOperation("保存一个新的后台用户")
    @PostMapping("save")
    public Result saveAdmin(@RequestBody Admin admin){
        admin.setPassword(MD5.encrypt(admin.getPassword()));
        adminService.save(admin);
        return Result.ok(null);
    }

    @ApiOperation("更新一个后台用户")
    @PutMapping("update")
    public Result updateAdmin(@RequestBody Admin admin){
        adminService.updateById(admin);
        return Result.ok(null);
    }

    @ApiOperation("获取某个用户的角色(包括查询所有角色)")
    @GetMapping("toAssign/{adminId}")
    public Result getAdminRoles(@PathVariable Long adminId){
        Map<String, Object> roleMap = roleService.getAdminRoles(adminId);//map中包括所有角色列表和用户已分配的角色列表
        return Result.ok(roleMap);
    }
    @ApiOperation("给某个用户分配角色")
    @PostMapping("doAssign")
    public Result assignAdminRoles(@RequestParam Long adminId,
                                   @RequestParam Long[] roleIds){
        roleService.assignAdminRoles(adminId, roleIds);
        return Result.ok(null);
    }

    @ApiOperation("删除某个用户")
    @DeleteMapping("remove/{id}")
    public Result removeAdminById(@PathVariable Long id){
        adminService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除多个用户")
    @DeleteMapping("batchRemove")
    public Result batchRemoveAdmins(@RequestBody List<Long> ids){
        adminService.removeByIds(ids);
        return Result.ok(null);
    }

}
