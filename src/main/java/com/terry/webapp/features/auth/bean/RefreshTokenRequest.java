package com.terry.webapp.features.auth.bean;

import javax.validation.constraints.NotEmpty;


public class RefreshTokenRequest {
	
//	@NotEmpty(message = "username can not be null")
//	private String username;
	
	@NotEmpty(message = "refresh token can not be null")
    private String refreshToken;

    @NotEmpty(message = "token can not be null")
    private String token;

//    public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}

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
