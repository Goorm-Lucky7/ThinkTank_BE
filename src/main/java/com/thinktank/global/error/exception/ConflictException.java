package com.thinktank.global.error.exception;

import com.thinktank.global.error.model.ErrorCode;

public class ConflictException extends ThinkTankException {
	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
