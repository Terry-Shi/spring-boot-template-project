package com.terry.webapp.security.token;

import java.util.Date;

/**
 *
 */
public class Token {
    private boolean valid = false;
    private String userId;
    private String wrappedToken;
    private Date expirationTime;

    public Token() {
        this(false, "", "", new Date());
    }

    public Token(boolean valid, String userId, String wrappedToken, Date expirationTime) {
        this.valid = valid;
        this.userId = userId;
        this.wrappedToken = wrappedToken;
        this.expirationTime = expirationTime;
    }
    
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWrappedToken(String wrappedToken) {
        this.wrappedToken = wrappedToken;
    }

    public String getWrappedToken() {
        return wrappedToken;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "Token{" +
                "valid=" + valid +
                ", userId='" + userId + '\'' +
                ", wrappedToken='" + wrappedToken + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
