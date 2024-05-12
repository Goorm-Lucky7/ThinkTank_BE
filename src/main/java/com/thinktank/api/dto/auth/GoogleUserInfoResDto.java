package com.thinktank.api.dto.auth;

public record GoogleUserInfoResDto(
	String id,
	String email,
	String name,
	boolean verifiedEmail,
	String givenName,
	String picture,
	String locale
) {
}
