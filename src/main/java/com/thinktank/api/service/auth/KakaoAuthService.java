package com.thinktank.api.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinktank.api.dto.auth.KakaoOAuthTokenDto;
import com.thinktank.api.dto.auth.KakaoUserInfoResDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String tokenRequestUri;

	@Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
	private String authorizationUri;

	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String userInfoUri;

	public String responseUri() {
		return authorizationUri
			+ "?client_id=" + clientId
			+ "&redirect_uri=" + redirectUri
			+ "&response_type=code";
	}

	public ResponseEntity<String> requestAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, httpHeaders);

		return restTemplate.postForEntity(tokenRequestUri, kakaoRequest, String.class);
	}

	public KakaoOAuthTokenDto getAccessToken(ResponseEntity<String> responseEntity) throws JsonProcessingException {
		return new ObjectMapper().readValue(responseEntity.getBody(), KakaoOAuthTokenDto.class);
	}

	public ResponseEntity<String> requestUserInfo(KakaoOAuthTokenDto kakaoOAuthTokenDto) {
		HttpHeaders httpHeaders = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		httpHeaders.add("Authorization", "Bearer " + kakaoOAuthTokenDto.accessToken());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

		return restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);
	}

	public KakaoUserInfoResDto getUserInfo(ResponseEntity<String> responseEntity) throws JsonProcessingException {
		return new ObjectMapper().readValue(responseEntity.getBody(), KakaoUserInfoResDto.class);
	}
}
