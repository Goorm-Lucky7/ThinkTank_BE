package com.thinktank.global.auth.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.common.util.CookieUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final int HTTP_STATUS_UNAUTHORIZED = 401;

	private static final long ACCESS_TOKEN_EXPIRATION_TIME_MS = 60 * 60 * 10L;

	private static final long REFRESH_TOKEN_EXPIRATION_TIME_MS = 60 * 60 * 24 * 1000L;

	private static final String ACCESS_TOKEN_HEADER = "access";

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

	private final AuthenticationManager authenticationManager;

	private final JwtProviderService jwtProviderService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
			username, password, null
		);

		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {

		String username = authResult.getName();

		String accessToken = jwtProviderService.createJwt(
			ACCESS_TOKEN_HEADER,
			username,
			ACCESS_TOKEN_EXPIRATION_TIME_MS
		);

		String refreshToken = jwtProviderService.createJwt(
			REFRESH_TOKEN_COOKIE_NAME,
			username,
			REFRESH_TOKEN_EXPIRATION_TIME_MS
		);

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
		response.addCookie(CookieUtils.createCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken));
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {

		response.setStatus(HTTP_STATUS_UNAUTHORIZED);
	}
}
