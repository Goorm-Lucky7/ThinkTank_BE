package com.thinktank.global.common.util;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {

	private static final int COOKIE_MAX_AGE_ONE_DAY = 24 * 60 * 60;

	public static Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(COOKIE_MAX_AGE_ONE_DAY);
		cookie.setHttpOnly(true);

		return cookie;
	}
}
