package com.terry.webapp.features.auth.bean;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Terry
 */
public class LoginRequest {

    @NotEmpty(message = "UserId can not be null")
    private String userId;

    @NotEmpty(message = "password can not be null")
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
