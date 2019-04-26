package com.terry.webapp.features.auth.bean;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class RefreshTokenRequest {
	@NotEmpty(message = "refresh token can not be null")
    private String refreshToken;

    @NotEmpty(message = "token can not be null")
    private String token;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
