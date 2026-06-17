package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blindbox.common.Result;
import com.blindbox.entity.SysUser;
import com.blindbox.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import cn.hutool.crypto.digest.BCrypt;

@RestController
@RequestMapping("/operator/user")
@RequiredArgsConstructor
public class OperatorUserController {

    private final SysUserMapper sysUserMapper;

    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String roleType,
                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (roleType != null && !roleType.isEmpty()) {
            wrapper.eq(SysUser::getRoleType, roleType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreatedTime);
        Page<SysUser> result = sysUserMapper.selectPage(new Page<>(page, pageSize), wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        user.setPassword(BCrypt.hashpw(user.getPassword() != null ? user.getPassword() : "123456"));
        sysUserMapper.insert(user);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
        return Result.success();
    }
}
