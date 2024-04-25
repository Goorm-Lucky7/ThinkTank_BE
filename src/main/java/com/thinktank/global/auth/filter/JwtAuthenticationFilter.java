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
import com.thinktank.global.error.model.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String ACCESS_TOKEN_HEADER = "access";

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
			setUnauthorizedResponse(response, ErrorCode.FAIL_TOKEN_EXPIRE_EXCEPTION.getMessage());
			return;
		}

		String category = jwtProviderService.getCategory(accessToken);
		if (!category.equals(ACCESS_TOKEN_HEADER)) {
			setUnauthorizedResponse(response, ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION.getMessage());
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

	private void setUnauthorizedResponse(HttpServletResponse response, String errorMessage) throws IOException {

		PrintWriter writer = response.getWriter();
		writer.print(errorMessage);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
