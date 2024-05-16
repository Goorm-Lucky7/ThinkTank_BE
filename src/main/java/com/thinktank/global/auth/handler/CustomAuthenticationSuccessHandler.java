package com.thinktank.global.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinktank.api.dto.auth.TokenResDto;
import com.thinktank.api.entity.auth.CustomOAuth2User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		String token = customOAuth2User.getToken();

		TokenResDto tokenResDto = new TokenResDto("Bearer " + token);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(tokenResDto));

		if(customOAuth2User.isNewUser()) response.setStatus(HttpServletResponse.SC_CREATED);
		else response.setStatus(HttpServletResponse.SC_OK);
	}
}
