package com.thinktank.api.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignupDTO(
	@NotBlank(message = "이메일을 입력해주세요.") String email,
	@NotBlank(message = "닉네임을 입력해주세요.") String nickname,
	@NotBlank(message = "비밀번호를 입력해주세요.") String password,
	@NotBlank(message = "확인 비밀번호를 입력해주세요") String checkPassword,
	String github,
	String blog,
	String introduce
) {
}
