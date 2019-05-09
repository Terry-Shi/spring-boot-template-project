package com.terry.webapp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myapp.authentication")
public class AppSecurityConfig {

	// 是否enable security
	private String enable;
	// token发布者
	private String issuer;
	// access token有效时长
	private int tokenExpiredSeconds;
	// refresh token有效时长
	private int refreshTokenExpiredSeconds;
	
	private String whiteListType;
	
	private String whiteList;

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public int getTokenExpiredSeconds() {
		return tokenExpiredSeconds;
	}

	public void setTokenExpiredSeconds(int tokenExpiredSeconds) {
		this.tokenExpiredSeconds = tokenExpiredSeconds;
	}

	public int getRefreshTokenExpiredSeconds() {
		return refreshTokenExpiredSeconds;
	}

	public void setRefreshTokenExpiredSeconds(int refreshTokenExpiredSeconds) {
		this.refreshTokenExpiredSeconds = refreshTokenExpiredSeconds;
	}

	public String getWhiteListType() {
		return whiteListType;
	}

	public void setWhiteListType(String whiteListType) {
		this.whiteListType = whiteListType;
	}

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

}
