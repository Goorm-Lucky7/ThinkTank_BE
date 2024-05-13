package com.thinktank.api.dto.user.response;

public record UserProfileResDto(
	String email,
	String nickname,
	String github,
	String blog,
	String introduce,
	String profileImage
) {
}
