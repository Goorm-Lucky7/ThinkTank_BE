package com.thinktank.api.dto.auth;

public record KakaoAccount(
	String email,
	boolean profileNicknameAgreement,
	boolean profileImageAgreement,
	KakaoProfile kakaoProfile,
	boolean nameAgreement,
	boolean emailAgreement,
	boolean hasEmail,
	boolean isEmailValid,
	boolean isEmailVerified
) {
}
