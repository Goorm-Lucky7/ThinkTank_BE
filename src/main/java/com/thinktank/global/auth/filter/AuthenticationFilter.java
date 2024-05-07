package com.thinktank.global.auth.filter;

import static com.thinktank.global.common.util.AuthConstants.*;
import static com.thinktank.global.common.util.GlobalConstant.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.thinktank.api.dto.auth.TokenResDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.auth.AuthorizationThreadLocal;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

	private final JwtProviderService jwtProviderService;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public AuthenticationFilter(
		JwtProviderService jwtProviderService,
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
	) {
		this.jwtProviderService = jwtProviderService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = jwtProviderService.extractAccessToken(ACCESS_TOKEN_HEADER, request);

			if (jwtProviderService.isUsable(accessToken)) {
				setAuthentication(accessToken);
				filterChain.doFilter(request, response);
			} else {
				String refreshToken = jwtProviderService.extractRefreshToken(REFRESH_TOKEN_COOKIE_NAME, request);

				if (jwtProviderService.isUsable(refreshToken)) {
					TokenResDto tokenResDto = jwtProviderService.reGenerateToken(refreshToken, response);
					accessToken = tokenResDto.accessToken();
					setAuthentication(accessToken);
					filterChain.doFilter(request, response);
				} else {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					handlerExceptionResolver.resolveException(request, response, null,
						new NotFoundException(ErrorCode.FAIL_TOKEN_EXPIRED_EXCEPTION));
				}
			}
		} finally {
			AuthorizationThreadLocal.remove();
		}
	}

	private void setAuthentication(String accessToken) {
		final AuthUser authUser = jwtProviderService.extractAuthUserByAccessToken(accessToken);
		final Authentication authToken = new UsernamePasswordAuthenticationToken(authUser, BLANK, null);

		SecurityContextHolder.getContext().setAuthentication(authToken);

		AuthorizationThreadLocal.setAuthUser(authUser);
	}
}
