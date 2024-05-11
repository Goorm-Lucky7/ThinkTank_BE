package com.thinktank.api.service.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinktank.api.dto.auth.KakaoOAuthTokenDto;
import com.thinktank.api.dto.auth.KakaoUserInfoResDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoAuthService kakaoAuthService;

	public KakaoUserInfoResDto kakaoLogin(String code) throws JsonProcessingException {
		return getKakaoUserInfoDto(code);
	}

	private KakaoUserInfoResDto getKakaoUserInfoDto(String code) throws JsonProcessingException {
		ResponseEntity<String> accessToken = kakaoAuthService.requestAccessToken(code);
		KakaoOAuthTokenDto kakaoOAuthTokenDto = kakaoAuthService.getAccessToken(accessToken);

		ResponseEntity<String> userInfoResponse = kakaoAuthService.requestUserInfo(kakaoOAuthTokenDto);

		return kakaoAuthService.getUserInfo(userInfoResponse);
	}
}
