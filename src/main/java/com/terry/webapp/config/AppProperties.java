package com.terry.webapp.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Terry
 */
@ConfigurationProperties(prefix = "myapp")
@Component
public class AppProperties {
	
    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    
    @PostConstruct
    public void print() {
    	System.out.print(toString());
    }

    @Override
    public String toString() {
        return "Properties{" +
                "authentication=" + authentication +
                '}';
    }

    public static class Authentication {
        private String issuer;
        private int tokenExpiredSeconds;
        private int refreshTokenExpiredSeconds;
        private boolean enable;

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

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        @Override
        public String toString() {
            return "Authentication{" +
                    "issuer='" + issuer + '\'' +
                    ", tokenExpiredSeconds=" + tokenExpiredSeconds +
                    ", refreshTokenExpiredSeconds=" + refreshTokenExpiredSeconds +
                    ", enable=" + enable +
                    '}';
        }
    }

}
