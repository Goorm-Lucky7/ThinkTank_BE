package com.thinktank.api.dto.auth;

public record KakaoLoginReqDto(
	String email,
	String nickname
) {
}
