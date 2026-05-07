package com.atclq.ssyx.user.controller;

import com.atclq.ssyx.common.result.Result;
import com.atclq.ssyx.model.user.Leader;
import com.atclq.ssyx.user.service.LeaderService;
import com.atclq.ssyx.vo.user.LeaderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "团长管理接口")
@RestController
@RequestMapping("/admin/user/leader")
public class LeaderController {

    @Autowired
    private LeaderService leaderService;

    @ApiOperation("获取待审核团长列表")
    @GetMapping("checkList/{page}/{limit}")
    public Result getCheckList(@PathVariable Long page, @PathVariable Long limit) {
        Page<Leader> pageParam = new Page<>(page, limit);
        IPage<Leader> pageModel = leaderService.selectCheckPage(pageParam, null);
        return Result.ok(pageModel);
    }

    @ApiOperation("获取团长列表")
    @GetMapping("list/{page}/{limit}")
    public Result getList(@PathVariable Long page, @PathVariable Long limit) {
        Page<Leader> pageParam = new Page<>(page, limit);
        IPage<Leader> pageModel = leaderService.selectPage(pageParam, null);
        return Result.ok(pageModel);
    }

    @ApiOperation("根据id查询团长")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id) {
        Leader leader = leaderService.getById(id);
        return Result.ok(leader);
    }

    @ApiOperation("新增团长")
    @PostMapping("save")
    public Result save(@RequestBody Leader leader) {
        leaderService.save(leader);
        return Result.ok(null);
    }

    @ApiOperation("修改团长")
    @PutMapping("update")
    public Result update(@RequestBody Leader leader) {
        leaderService.updateById(leader);
        return Result.ok(null);
    }

    @ApiOperation("删除团长")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        leaderService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除团长")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        leaderService.removeByIds(ids);
        return Result.ok(null);
    }

    @ApiOperation("审核团长")
    @PostMapping("check/{id}/{status}")
    public Result check(@PathVariable Long id, @PathVariable Integer status) {
        leaderService.check(id, status);
        return Result.ok(null);
    }
}
