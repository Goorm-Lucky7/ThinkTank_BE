package com.thinktank.global.common.util;

import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {

	private static final int COOKIE_MAX_AGE_ONE_DAY = 24 * 60 * 60;

	public static Cookie tokenCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(COOKIE_MAX_AGE_ONE_DAY);
		cookie.setHttpOnly(true);

		return cookie;
	}

	public static Optional<String> findCookieValue(HttpServletRequest request, String cookieName) {

		if (request.getCookies() == null) {
			return Optional.empty();
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(cookieName))
			.findFirst()
			.map(Cookie::getValue);
	}

	public static void deleteCookie(String cookieName, HttpServletResponse response) {

		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
