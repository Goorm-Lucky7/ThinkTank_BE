package com.thinktank.api.dto.judge.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JudgeDto(
	@NotNull
	Long postId,
	@NotBlank(message = "[❎ ERROR] 언어는 필수 입력값입니다")
	String language,
	@NotBlank(message = "[❎ ERROR] 정답 코드는 필수 입력값입니다.")
	@Length(max = 1000, message = "[❎ ERROR] 코드는 최대 1000자 입니다.")
	String code
) {
}
