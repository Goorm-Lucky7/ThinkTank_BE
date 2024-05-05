package com.thinktank.api.dto.user.response;

import com.thinktank.api.dto.profileImage.response.ProfileImageResDto;

public record UserResDto(
	String email,
	String nickname,
	String github,
	String blog,
	String introduce,
	ProfileImageResDto profileImageResDto
) {
}
