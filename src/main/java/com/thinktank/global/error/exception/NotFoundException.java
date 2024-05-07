package com.thinktank.global.error.exception;

import com.thinktank.global.error.model.ErrorCode;

public class NotFoundException extends ThinkTankException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
