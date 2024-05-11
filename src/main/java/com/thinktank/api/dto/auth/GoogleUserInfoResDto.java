package com.thinktank.api.dto.auth;

public record GoogleUserInfoResDto(
	String id,
	String email,
	String name,
	String picture
) {
}
