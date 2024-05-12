package com.atclq.ssyx.acl.service.impl;

import com.atclq.ssyx.acl.mapper.AdminMapper;
import com.atclq.ssyx.acl.service.AdminService;
import com.atclq.ssyx.model.acl.Admin;
import com.atclq.ssyx.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;//这里可以导入使用adminMapper，也可以直接用baseMapper，都可以。ctrl+鼠标左键查看到具体baseMapper的实现类，发现baseMapper代替了相应的mapper

    @Override
    public IPage<Admin> selectAdminPage(Page<Admin> adminPage, AdminQueryVo adminQueryVo) {
        //获取条件值用户名称 注意两个都要获取
        String username = adminQueryVo.getUsername();
        String name = adminQueryVo.getName();
        //创建条件构造器
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        //如果不为空，则封装条件
        if(username!= null && !username.equals(" ")){
            wrapper.like(Admin::getUsername, username);
        }
        if(name!= null && !name.equals(" ")){
            wrapper.like(Admin::getName, name);
        }
        //调用Basemapper方法selectPage方法实现条件分页查询
        IPage<Admin> adminIPage = adminMapper.selectPage(adminPage, wrapper);

        return adminIPage;
    }
}
