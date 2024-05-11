package com.thinktank.api.dto.auth;

public record KakaoProfile(
	String nickname,
	String thumbnailImageUrl,
	String profileImageUrl,
	boolean isDefaultImage
) {
}
