package com.thinktank.global.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thinktank.global.auth.jwt.JwtTokenProvider;
import com.thinktank.global.auth.service.ClientDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final int HTTP_STATUS_UNAUTHORIZED = 401;

	private static final long TOKEN_EXPIRATION_TIME_MS = 60 * 60 * 10L;

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String TOKEN_PREFIX = "Bearer ";

	private final AuthenticationManager authenticationManager;

	private final JwtTokenProvider jwtTokenProvider;

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

		ClientDetails clientDetails = (ClientDetails)authResult.getPrincipal();

		String username = clientDetails.getUsername();
		String token = jwtTokenProvider.createJwt(username, TOKEN_EXPIRATION_TIME_MS);

		response.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + token);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {

		response.setStatus(HTTP_STATUS_UNAUTHORIZED);
	}
}
