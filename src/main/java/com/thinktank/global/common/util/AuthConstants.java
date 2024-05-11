package com.thinktank.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {

	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "refresh";
	public static final String BEARER = "Bearer";

	public static final String CLIENT_ID = "?client_id=";
	public static final String REDIRECT_URI = "&redirect_uri=";
	public static final String RESPONSE_TYPE = "&response_type=code";
	public static final String SCOPE = "&scope=";
	public static final String INFO = "email profile";
}
