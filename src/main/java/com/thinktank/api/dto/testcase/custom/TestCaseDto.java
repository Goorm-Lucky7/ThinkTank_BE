package com.thinktank.api.dto.testcase.custom;

import jakarta.validation.constraints.NotBlank;

public record TestCaseDto(
	@NotBlank
	String example,

	@NotBlank
	String result
) {
}
