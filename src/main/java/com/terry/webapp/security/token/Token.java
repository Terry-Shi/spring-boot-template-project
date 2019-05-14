package com.terry.webapp.security.token;

import java.util.Date;

/**
 *
 */
public class Token {
    private boolean valid = false;
    private String username;
//    private String wrappedToken;
    private Date expirationTime;

    public Token() {
        this(false, "", "", new Date());
    }

    public Token(boolean valid, String username, String wrappedToken, Date expirationTime) {
        this.valid = valid;
        this.username = username;
//        this.wrappedToken = wrappedToken;
        this.expirationTime = expirationTime;
    }
    
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public void setWrappedToken(String wrappedToken) {
//        this.wrappedToken = wrappedToken;
//    }
//
//    public String getWrappedToken() {
//        return wrappedToken;
//    }

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
                ", username='" + username + '\'' +
//                ", wrappedToken='" + wrappedToken + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
