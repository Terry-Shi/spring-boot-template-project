package com.terry.webapp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.terry.webapp.common.response.BaseResponse;

@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler({ AppException.class })
	@ResponseBody
	public ResponseEntity<BaseResponse> handleAppException(AppException e) {
        BaseResponse response = new BaseResponse(500, e.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 数据校验失败
	 * @param e
	 * @return
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseBody
	public ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException e) {
		StringBuilder sb = new StringBuilder("Validation failed: ");

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
			sb.append("[").append(error.getDefaultMessage()).append("] ");
		}
        BaseResponse response = new BaseResponse(400, sb.toString());
	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ Exception.class })
	@ResponseBody
	public ResponseEntity<BaseResponse> handleException(Exception e) {
        BaseResponse response = new BaseResponse(500, e.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	
}
