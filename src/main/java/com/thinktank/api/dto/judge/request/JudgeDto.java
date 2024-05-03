package com.thinktank.api.dto.judge.request;

import java.util.List;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JudgeDto(
	@NotNull
	Long postId,
	@NotBlank(message = "[❎ ERROR] 언어는 필수 입력값입니다")
	String language,

	List<CustomTestCase> testCases,
	@NotBlank(message = "[❎ ERROR] 정답 코드는 필수 입력값입니다.")
	String code
) {
}
