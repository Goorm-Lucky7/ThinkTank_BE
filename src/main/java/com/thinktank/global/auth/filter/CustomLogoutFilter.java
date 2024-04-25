package com.thinktank.global.auth.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.filter.GenericFilterBean;

import com.thinktank.api.repository.auth.TokenRepository;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.common.util.CookieUtils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

	private static final String LOGOUT_PATH = "/logout";

	private static final String POST_METHOD = "POST";

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

	private final JwtProviderService jwtProviderService;

	private final TokenRepository tokenRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws
		IOException, ServletException {

		doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException, ServletException {

		if (isLogoutRequest(request)) {
			handleLogout(request, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private boolean isLogoutRequest(HttpServletRequest request) {

		return request.getRequestURI()
			.matches(LOGOUT_PATH) && POST_METHOD.equals(request.getMethod());
	}

	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws
		IOException, ServletException {

		Optional<String> refreshToken = CookieUtils.findCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

		if (refreshToken.isEmpty() || !validateRefreshToken(refreshToken.get()) || !tokenExistsInDb(
			refreshToken.get())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		tokenRepository.deleteByRefreshToken(refreshToken.get());
		CookieUtils.deleteCookie(REFRESH_TOKEN_COOKIE_NAME, response);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private boolean validateRefreshToken(String refreshToken) {

		try {
			jwtProviderService.isExpired(refreshToken);
			return REFRESH_TOKEN_COOKIE_NAME.equals(jwtProviderService.getCategory(refreshToken));
		} catch (ExpiredJwtException e) {
			return false;
		}
	}

	private boolean tokenExistsInDb(String refreshToken) {

		return tokenRepository.existsByRefreshToken(refreshToken);
	}
}
