package com.blindbox.service;

import com.blindbox.dto.LoginDTO;
import com.blindbox.vo.LoginVO;
import com.blindbox.vo.UserInfoVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    UserInfoVO getCurrentUser();
}
