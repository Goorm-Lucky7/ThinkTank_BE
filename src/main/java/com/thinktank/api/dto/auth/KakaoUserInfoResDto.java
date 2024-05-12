package com.thinktank.api.dto.auth;

public record KakaoUserInfoResDto(
	String id,
	String connectedAt,
	Properties properties,
	Account account
) {

	public record Properties(
		String nickname,
		String profileImage,
		String thumbnailImage
	) {
	}

	public record Account(
		String email,
		boolean profileNicknameAgreement,
		boolean profileImageAgreement,
		boolean nameAgreement,
		boolean emailAgreement,
		boolean hasEmail,
		boolean isEmailValid,
		boolean isEmailVerified,
		Profile profile
	) {

		public record Profile(
			String nickname,
			String thumbnailImageUrl,
			String profileImageUrl,
			boolean isDefaultImage
		) {
		}
	}
}
