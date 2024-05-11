package com.thinktank.api.dto.auth;

public record GoogleUserInfoDto(
	String id,
	String email,
	boolean verifiedEmail,
	String name,
	String givenName,
	String picture,
	String locale
) {
}
