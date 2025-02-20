package com.truong.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
	USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1003, "User not found", HttpStatus.BAD_REQUEST ),
	INVALID_PASSWORD(1004, "Invalid password", HttpStatus.BAD_REQUEST),
	INVALID_ID(1005, "Invalid id", HttpStatus.BAD_REQUEST),
	INVALID_USER(1006, "Invalid user", HttpStatus.BAD_REQUEST),
	WRONG_PASSWORD(1007, "Wrong password", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED_ACCESS(1008,"Unauthorized",HttpStatus.UNAUTHORIZED),
	DEPARTMENT_EXISTED(1009, "Department existed",HttpStatus.BAD_REQUEST),
    DEPARTMENT_NOT_FOUND(1010, "Department not found", HttpStatus.NOT_FOUND);
	
	private final int code;
	private ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	private final String message;
	private final HttpStatusCode statusCode;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}
}
