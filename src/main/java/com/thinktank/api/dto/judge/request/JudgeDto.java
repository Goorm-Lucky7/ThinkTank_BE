package com.thinktank.api.dto.judge.request;

import java.util.List;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JudgeDto(
	@NotNull
	Long postId,

	@NotNull
	List<CustomTestCase> testCases,
	@NotBlank
	String language,

	@NotBlank
	String code
) {
}
