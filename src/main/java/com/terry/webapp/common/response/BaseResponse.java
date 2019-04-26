package com.terry.webapp.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 */
public class BaseResponse {

    @JsonProperty("status")
    protected int statusCode;
    protected String message;

    public BaseResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }


    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

}
