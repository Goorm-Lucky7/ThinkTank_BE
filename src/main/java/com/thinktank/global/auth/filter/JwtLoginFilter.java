package com.thinktank.global.auth.filter;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thinktank.api.service.auth.AuthorizationService;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.common.util.CookieUtils;
import com.thinktank.global.config.TokenConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final TokenConfig tokenConfig;

	private final AuthenticationManager authenticationManager;

	private final JwtProviderService jwtProviderService;

	private final AuthorizationService authorizationService;

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

		String accessToken = jwtProviderService.provideAccessToken(
			ACCESS_TOKEN_HEADER,
			username,
			tokenConfig.getAccessTokenExpirationTimeMs()
		);
		String refreshToken = jwtProviderService.provideRefreshToken(
			REFRESH_TOKEN_COOKIE_NAME,
			username,
			tokenConfig.getRefreshTokenExpirationTimeMs()
		);

		authorizationService.addRefreshToken(username, refreshToken, tokenConfig.getRefreshTokenExpirationTimeMs());

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);

		Cookie refreshTokenCookie = CookieUtils.tokenCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
		response.addCookie(refreshTokenCookie);

		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
