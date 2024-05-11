package com.thinktank.api.dto.auth;

public record KakaoUserInfoResDto(
	String id,
	String connectedAt,
	KakaoProperties kakaoProperties,
	KakaoAccount kakaoAccount
) {
}
