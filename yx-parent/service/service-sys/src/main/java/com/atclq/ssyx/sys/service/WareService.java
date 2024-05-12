package com.atclq.ssyx.sys.service;

import com.atclq.ssyx.model.sys.Ware;
import com.atclq.ssyx.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WareService extends IService<Ware> {

    IPage<Ware> selectWarePage(Page<Ware> warePage, WareQueryVo wareQueryVo);
}
