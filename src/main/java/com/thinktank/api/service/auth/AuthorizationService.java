package com.thinktank.api.service.auth;

import org.springframework.stereotype.Service;

import com.thinktank.global.common.util.CookieUtils;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

	private static final String ACCESS_TOKEN_TYPE = "access";

	private static final long ACCESS_TOKEN_EXPIRY_DURATION = 600000L;

	private final JwtProviderService jwtProviderService;

	public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = CookieUtils.findCookieValue(request, REFRESH_TOKEN_COOKIE_NAME)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION));

		validateTokenNotExpired(refreshToken);
		validateTokenIsRefresh(refreshToken);

		String username = jwtProviderService.getUsername(refreshToken);
		String newAccess = jwtProviderService.createJwt(
			ACCESS_TOKEN_TYPE,
			username,
			ACCESS_TOKEN_EXPIRY_DURATION
		);

		response.setHeader(ACCESS_TOKEN_TYPE, newAccess);
	}

	private void validateTokenNotExpired(String refreshToken) {

		if (jwtProviderService.isExpired(refreshToken)) {
			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRE_EXCEPTION);
		}
	}

	private void validateTokenIsRefresh(String refreshToken) {

		if (!jwtProviderService.getCategory(refreshToken).equals(REFRESH_TOKEN_COOKIE_NAME)) {
			throw new UnauthorizedException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION);
		}
	}
}
