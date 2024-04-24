package com.thinktank.global.error.exception;

import com.thinktank.global.error.model.ErrorCode;

public class UnauthorizedException extends ThinkTankException {
	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
