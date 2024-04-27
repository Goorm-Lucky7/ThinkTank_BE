package com.thinktank.global.auth.filter;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thinktank.api.service.auth.AuthorizationService;
import com.thinktank.api.service.auth.JwtAuthenticationService;
import com.thinktank.global.error.exception.UnauthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

	private final JwtAuthenticationService jwtAuthenticationService;

	private final AuthorizationService authorizationService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			jwtAuthenticationService.validateToken(accessToken, response);
			setAuthentication(accessToken);
			filterChain.doFilter(request, response);
		} catch (UnauthorizedException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void setAuthentication(String accessToken) {

		Authentication authentication = authorizationService.getAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
