package com.thinktank.api.entity.auth;

public record AuthUser(
	String email,
	String nickname
) {

	public static AuthUser create(String email, String nickname) {
		return new AuthUser(email, nickname);
	}
}
