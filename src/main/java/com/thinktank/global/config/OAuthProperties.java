package com.thinktank.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
@Getter
public class OAuthProperties {

	@Value("${KAKAO_CLIENT_ID}")
	private String kakaoClientId;

	@Value("${KAKAO_CLIENT_SECRET}")
	private String kakaoClientSecret;

	@Value("${KAKAO_REDIRECT_URI}")
	private String kakaoRedirectUri;

	@PostConstruct
	public void init() {
	}
}
