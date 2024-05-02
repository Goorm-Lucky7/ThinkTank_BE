package com.thinktank.api.dto.testcase.custom;

import jakarta.validation.constraints.NotBlank;

public record CustomTestCase(
	@NotBlank
	String example,

	@NotBlank
	String result
) {
}
