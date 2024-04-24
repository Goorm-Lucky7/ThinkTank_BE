package com.thinktank.global.error.exception;

import com.thinktank.global.error.model.ErrorCode;

public class ThinkTankException extends RuntimeException {
	private ErrorCode errorCode;

	public ThinkTankException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
