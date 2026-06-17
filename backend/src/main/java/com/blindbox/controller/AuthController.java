package com.blindbox.controller;

import com.blindbox.common.Result;
import com.blindbox.dto.LoginDTO;
import com.blindbox.service.AuthService;
import com.blindbox.vo.LoginVO;
import com.blindbox.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @GetMapping("/profile")
    public Result<UserInfoVO> getProfile() {
        return Result.success(authService.getCurrentUser());
    }
}
