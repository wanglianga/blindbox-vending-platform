package com.blindbox.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String roleType;
    private Integer status;
    private Long companyId;
    private LocalDateTime createdTime;
}
