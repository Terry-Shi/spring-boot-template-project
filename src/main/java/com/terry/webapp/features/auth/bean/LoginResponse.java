package com.terry.webapp.features.auth.bean;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import com.terry.webapp.common.response.BaseResponse;

/**
 *
 */
public class LoginResponse extends BaseResponse {
    private String token;
    private String refreshToken;
    private String userId;
    private String sysRoles; // SYS_ADMIN,GUEST,......

    private LoginResponse(Builder builder) {
        super(builder.statusCode, builder.message);
        this.token = builder.token;
        this.refreshToken = builder.refreshToken;
        this.userId = builder.userId;
        this.sysRoles = builder.sysRoles;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    
    public String getUserId() {
        return userId;
    }

    public String getSysRoles() {
        return sysRoles;
    }

    public static class Builder {
        private static Map<Integer, String> messages = ImmutableMap.of(
        		200, "You have logged in successfully", 
        		401, "The username or password is incorrect");
        private int statusCode;
        private String token; // accessToken
        private String refreshToken;
        private String message;
        private String userId;
        private String sysRoles;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode == 201 ? 200 : statusCode;
            return this;
        }
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder sysRoles(String sysRoles) {
            this.sysRoles = sysRoles;
            return this;
        }
        
        public LoginResponse build() {
            if (message == null)
                this.message = messages.getOrDefault(statusCode,  "服务器端错误！");
            return new LoginResponse(this);
        }
    }
}
