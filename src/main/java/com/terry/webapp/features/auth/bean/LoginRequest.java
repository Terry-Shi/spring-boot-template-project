package com.terry.webapp.features.auth.bean;

import javax.validation.constraints.NotEmpty;


/**
 * @author Terry
 */
public class LoginRequest {

	@NotEmpty(message = "用户名不能为空")
    private String username;

	@NotEmpty(message = "密码不能为空")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
