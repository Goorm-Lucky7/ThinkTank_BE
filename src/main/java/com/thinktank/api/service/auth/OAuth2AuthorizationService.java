package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;
import static com.thinktank.global.common.util.GlobalConstant.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinktank.api.dto.auth.GoogleUserInfoResDto;
import com.thinktank.api.dto.auth.KakaoUserInfoResDto;
import com.thinktank.api.dto.auth.OAuthTokenResDto;
import com.thinktank.global.config.RestTemplateConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2AuthorizationService {

	private final RestTemplateConfig restTemplateConfig;
	private final ObjectMapper objectMapper;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
	private String kakaoAuthorizationUri;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String kakaoTokenRequestUri;

	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String kakaoUserInfoUri;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String googleClientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String googleClientSecret;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;

	@Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
	private String googleAuthorizationUri;

	@Value("${spring.security.oauth2.client.provider.google.token-uri}")
	private String googleTokenRequestUri;

	@Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
	private String googleUserInfoUri;

	public ResponseEntity<String> requestKakaoAccessToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", kakaoClientId);
		params.add("redirect_uri", kakaoRedirectUri);
		params.add("code", code);

		return sendTokenRequest(params, kakaoTokenRequestUri);
	}

	public ResponseEntity<String> requestGoogleAccessToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", googleClientId);
		params.add("client_secret", googleClientSecret);
		params.add("redirect_uri", googleRedirectUri);
		params.add("code", code);

		return sendTokenRequest(params, googleTokenRequestUri);
	}

	public OAuthTokenResDto getKakaoAccessToken(ResponseEntity<String> responseEntity) {
		return convertToDto(responseEntity, OAuthTokenResDto.class);
	}

	public OAuthTokenResDto getGoogleAccessToken(ResponseEntity<String> responseEntity) {
		return convertToDto(responseEntity, OAuthTokenResDto.class);
	}

	public ResponseEntity<String> requestKakaoUserInfo(OAuthTokenResDto oAuthTokenResDto) {
		return requestUserInfo(oAuthTokenResDto.accessToken(), kakaoUserInfoUri);
	}

	public ResponseEntity<String> requestGoogleUserInfo(OAuthTokenResDto OAuthTokenResDto) {
		return requestUserInfo(OAuthTokenResDto.accessToken(), googleUserInfoUri);
	}

	public KakaoUserInfoResDto getKakaoUserInfo(ResponseEntity<String> responseEntity) {
		return convertToDto(responseEntity, KakaoUserInfoResDto.class);
	}

	public GoogleUserInfoResDto getGoogleUserInfo(ResponseEntity<String> responseEntity) {
		return convertToDto(responseEntity, GoogleUserInfoResDto.class);
	}

	public String responseKakaoUri() {
		return buildAuthorizationUri(
			kakaoAuthorizationUri,
			kakaoClientId,
			kakaoRedirectUri,
			null
		);
	}

	public String responseGoogleUri() {
		return buildAuthorizationUri(
			googleAuthorizationUri,
			googleClientId,
			googleRedirectUri,
			INFO
		);
	}

	private ResponseEntity<String> sendTokenRequest(MultiValueMap<String, String> params, String uri) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		params.add("grant_type", "authorization_code");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);

		return restTemplateConfig.restTemplate().exchange(uri, HttpMethod.POST, request, String.class);
	}

	private <T> T convertToDto(ResponseEntity<String> responseEntity, Class<T> clazz) {
		try {
			return objectMapper.readValue(responseEntity.getBody(), clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private ResponseEntity<String> requestUserInfo(String accessToken, String userInfoUri) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(ACCESS_TOKEN_HEADER, BEARER + BLANK + accessToken);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

		return restTemplateConfig.restTemplate().exchange(userInfoUri, HttpMethod.GET, request, String.class);
	}

	private String buildAuthorizationUri(String baseUri, String clientId, String redirectUri, String info) {
		String uri = baseUri + CLIENT_ID + clientId + REDIRECT_URI + redirectUri + RESPONSE_TYPE;

		if (info != null)
			uri += SCOPE + info;

		return uri;
	}
}
