package com.service.webapp.common.exception;

import org.springframework.core.NestedRuntimeException;

public class RestapiException extends NestedRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1322430030852273452L;
	
	private int statusCode;
	
	public RestapiException(int statusCode, String msg) {
		super(msg);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
