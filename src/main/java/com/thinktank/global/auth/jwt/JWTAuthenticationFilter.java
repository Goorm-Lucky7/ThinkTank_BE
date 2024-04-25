package com.thinktank.global.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thinktank.api.entity.User;
import com.thinktank.global.auth.service.ClientDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JWTTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.split(" ")[1];

		if (jwtTokenProvider.isExpired(token)) {

			filterChain.doFilter(request, response);
			return;
		}

		String username = jwtTokenProvider.getUsername(token);

		User user = User.builder()
			.email(username)
			.password("temppassword")
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
