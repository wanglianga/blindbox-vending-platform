package com.blindbox.interceptor;

import com.blindbox.common.JwtUtils;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.entity.SysUser;
import com.blindbox.mapper.SysUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final SysUserMapper sysUserMapper;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                writeErrorResponse(response, ResultCode.UNAUTHORIZED);
                return false;
            }

            SysUser user = sysUserMapper.selectById(userId);
            if (user == null || user.getStatus() == 0) {
                writeErrorResponse(response, ResultCode.USER_DISABLED);
                return false;
            }

            UserContext.UserInfo userInfo = new UserContext.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRoleType(),
                    user.getCompanyId()
            );
            UserContext.setUser(userInfo);

            return true;
        } catch (Exception e) {
            log.error("认证失败", e);
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> result = new HashMap<>();
        result.put("code", resultCode.getCode());
        result.put("msg", resultCode.getMsg());
        result.put("data", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
