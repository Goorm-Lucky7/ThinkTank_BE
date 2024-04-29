package com.thinktank.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {

	public static final String ACCESS_TOKEN_HEADER = "access";
	public static final String REFRESH_TOKEN_HEADER = "refresh";
	public static final String BEARER = "Bearer";
}
