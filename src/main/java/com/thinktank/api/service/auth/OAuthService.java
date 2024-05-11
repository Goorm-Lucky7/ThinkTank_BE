package com.thinktank.api.service.auth;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinktank.api.dto.auth.KakaoOAuthTokenDto;
import com.thinktank.api.dto.auth.KakaoUserInfoResDto;
import com.thinktank.api.dto.auth.GoogleOAuthTokenDto;
import com.thinktank.api.dto.auth.GoogleUserInfoResDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoAuthService kakaoAuthService;
	private final GoogleAuthService googleAuthService;

	public KakaoUserInfoResDto kakaoLogin(String code) throws JsonProcessingException {
		return getKakaoUserInfoDto(code);
	}

	public GoogleUserInfoResDto googleLogin(String code) throws IOException {
		return getGoogleUserInfoDto(code);
	}

	private KakaoUserInfoResDto getKakaoUserInfoDto(String code) throws JsonProcessingException {
		ResponseEntity<String> accessToken = kakaoAuthService.requestAccessToken(code);
		KakaoOAuthTokenDto kakaoOAuthTokenDto = kakaoAuthService.getAccessToken(accessToken);

		ResponseEntity<String> userInfoResponse = kakaoAuthService.requestUserInfo(kakaoOAuthTokenDto);

		return kakaoAuthService.getUserInfo(userInfoResponse);
	}

	private GoogleUserInfoResDto getGoogleUserInfoDto(String code) throws JsonProcessingException {
		ResponseEntity<String> accessToken = googleAuthService.requestAccessToken(code);
		GoogleOAuthTokenDto googleOAuthTokenDto = googleAuthService.getAccessToken(accessToken);

		ResponseEntity<String> userInfoResponse = googleAuthService.requestUserInfo(googleOAuthTokenDto);

		return googleAuthService.getUserInfo(userInfoResponse);
	}
}
