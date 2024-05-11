package com.thinktank.api.service.auth;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinktank.api.dto.auth.GoogleOAuthTokenDto;
import com.thinktank.api.dto.auth.GoogleUserInfoResDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.provider.google.token-uri}")
	private String tokenRequestUri;

	@Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
	private String authorizationUri;

	@Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
	private String userInfoUri;

	public String getOAuthRedirectURL() {
		return authorizationUri
			+ "?client_id=" + clientId
			+ "&redirect_uri=" + redirectUri
			+ "&scope=email profile"
			+ "&response_type=code";
	}

	public ResponseEntity<String> requestAccessToken(String code) {
		String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("code", decodedCode);

		HttpEntity<MultiValueMap<String, String>> googleRequest = new HttpEntity<>(params, httpHeaders);

		return restTemplate.exchange(tokenRequestUri, HttpMethod.POST, googleRequest, String.class);
	}

	public GoogleOAuthTokenDto getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
		return new ObjectMapper().readValue(response.getBody(), GoogleOAuthTokenDto.class);
	}

	public ResponseEntity<String> requestUserInfo(GoogleOAuthTokenDto googleOAuthTokenDto) {
		HttpHeaders httpHeaders = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		httpHeaders.add("Authorization", "Bearer " + googleOAuthTokenDto.accessToken());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

		return restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);
	}

	public GoogleUserInfoResDto getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
		return new ObjectMapper().readValue(response.getBody(), GoogleUserInfoResDto.class);
	}
}
