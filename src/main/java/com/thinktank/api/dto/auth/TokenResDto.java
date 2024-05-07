package com.thinktank.api.dto.auth;

public record TokenResDto(
	String accessToken,
	String refreshToken
) {
}
