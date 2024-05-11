package com.thinktank.global.auth.filter;

import static com.thinktank.global.common.util.AuthConstants.*;
import static com.thinktank.global.common.util.GlobalConstant.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.auth.AuthorizationThreadLocal;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.FilterChain;
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
		@NotNull FilterChain filterChain
	) {
		String accessToken = jwtProviderService.extractToken(ACCESS_TOKEN_HEADER, request);
		String refreshToken = jwtProviderService.extractToken(REFRESH_TOKEN_HEADER, request);

		try {
			if (jwtProviderService.isUsable(accessToken)) {
				setAuthentication(accessToken);
				filterChain.doFilter(request, response);

				return;
			}

			if (jwtProviderService.isUsable(refreshToken)) {
				accessToken = jwtProviderService.reGenerateToken(refreshToken, response);
				setAuthentication(accessToken);
				filterChain.doFilter(request, response);

				return;
			}

			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRED_EXCEPTION);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			handlerExceptionResolver.resolveException(request, response, null, e);
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
