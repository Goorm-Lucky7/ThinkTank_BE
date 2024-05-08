package com.thinktank.api.dto.user.response;

public record UserProfileResDto(
	String email,
	String nickName,
	String profileImage,
	String introduce
) {
}
