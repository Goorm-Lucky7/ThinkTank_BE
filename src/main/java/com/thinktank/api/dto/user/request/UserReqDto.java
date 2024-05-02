package com.thinktank.api.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UserReqDto(
	@NotBlank(message = "[❎ ERROR] 비밀번호를 입력해주세요.")
	String password
) {
}
