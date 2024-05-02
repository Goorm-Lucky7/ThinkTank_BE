package com.thinktank.api.dto.user.response;

public record UserResDto(
	String email,
	String nickname,
	String github,
	String blog,
	String introduce
) {
}
