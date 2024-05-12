package com.atclq.ssyx.acl.service;

import com.atclq.ssyx.model.acl.Admin;
import com.atclq.ssyx.vo.acl.AdminLoginVo;
import com.atclq.ssyx.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdminService extends IService<Admin> {
    IPage<Admin> selectAdminPage(Page<Admin> adminPage, AdminQueryVo adminQueryVo);
}
