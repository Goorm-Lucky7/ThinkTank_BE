package com.thinktank.api.dto.auth;

public record OAuthLoginReqDto(
	String email,
	String nickname,
	String imageUrl
) {
}
