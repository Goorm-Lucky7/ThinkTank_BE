package com.thinktank.global.auth.filter;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thinktank.api.entity.User;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.auth.service.ClientDetails;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private static final String ACCESS_TOKEN_HEADER = "access";

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

	private final JwtProviderService jwtProviderService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

		if (accessToken == null) {

			filterChain.doFilter(request, response);
			return;
		}

		try {
			jwtProviderService.isExpired(accessToken);
		} catch (ExpiredJwtException e) {

			PrintWriter writer = response.getWriter();
			writer.print("ACCESS TOKEN EXPIRED");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String category = jwtProviderService.getCategory(accessToken);

		if (!category.equals(ACCESS_TOKEN_HEADER)) {

			PrintWriter writer = response.getWriter();
			writer.print("INVALID ACCESS TOKEN");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String username = jwtProviderService.getUsername(accessToken);

		User user = User.builder()
			.email(username)
			.build();

		ClientDetails clientDetails = new ClientDetails(user);

		Authentication authToken = new UsernamePasswordAuthenticationToken(
			clientDetails,
			null,
			clientDetails.getAuthorities()
		);

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
