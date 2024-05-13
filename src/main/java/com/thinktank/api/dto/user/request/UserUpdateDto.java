package com.thinktank.api.dto.user.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
	@Size(min = 2, max = 10, message = "[❎ ERROR] 닉네임은 2글자에서 10글자 사이여야 합니다.")
	@Pattern(regexp = "^[A-Za-z\\d]+$", message = "[❎ ERROR] 닉네임은 영문과 숫자만 사용가능합니다.")
	String nickname,

	String github,

	String blog,

	String introduce,

	String profileImage
) {
}
