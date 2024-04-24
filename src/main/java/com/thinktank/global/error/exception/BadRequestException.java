package com.thinktank.global.error.exception;

import com.thinktank.global.error.model.ErrorCode;

public class BadRequestException extends ThinkTankException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
