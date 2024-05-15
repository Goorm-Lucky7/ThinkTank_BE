package com.thinktank.api.dto.user.response;

public record SimpleUserResDto(
	String email,
	String nickname,
	String profileImage
) {
}
