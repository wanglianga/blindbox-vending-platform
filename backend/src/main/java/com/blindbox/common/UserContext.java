package com.blindbox.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        USER_HOLDER.set(userInfo);
    }

    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        UserInfo user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo user = getUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getRoleType() {
        UserInfo user = getUser();
        return user != null ? user.getRoleType() : null;
    }

    public static Long getCompanyId() {
        UserInfo user = getUser();
        return user != null ? user.getCompanyId() : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String roleType;
        private Long companyId;
    }
}
