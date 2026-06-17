package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blindbox.common.BusinessException;
import com.blindbox.common.JwtUtils;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.dto.LoginDTO;
import com.blindbox.entity.SysUser;
import com.blindbox.mapper.SysUserMapper;
import com.blindbox.service.AuthService;
import com.blindbox.vo.LoginVO;
import com.blindbox.vo.UserInfoVO;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, loginDTO.getUsername())
        );

        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleType());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        loginVO.setUserInfo(userInfoVO);

        return loginVO;
    }

    @Override
    public UserInfoVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
}
