package com.thinktank.api.entity.auth;

import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

public enum OAuthProvider {
	KAKAO;

	public static OAuthProvider findByName(String name) {
		for(OAuthProvider oauthProvider : values()) {
			if(oauthProvider.name().equalsIgnoreCase(name)) {
				return oauthProvider;
			}
		}
		throw new NotFoundException(ErrorCode.FAIL_REGISTRATION_NOT_FOUND);
	}
}
