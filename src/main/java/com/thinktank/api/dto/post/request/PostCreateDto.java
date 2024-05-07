package com.thinktank.api.dto.post.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCreateDto(
	@NotBlank @Length(max = 20, message = "[❎ ERROR] 제목은 최대 20자 입니다.")
	String title,

	@NotBlank(message = "[❎ ERROR] 카테고리는 필수 입력값입니다.")
	String category,

	@NotBlank(message = "[❎ ERROR] 언어는 필수 입력값입니다")
	String language,

	@NotBlank(message = "[❎ ERROR] 문제 설명은 필수 입력값입니다.")
	String content,

	@NotBlank(message = "[❎ ERROR] 제약 조건은 필수 입력값입니다.")
	String condition,
	@NotNull(message = "[❎ ERROR] 테스트케이스는 필수 입력값입니다.")
	List<CustomTestCase> testCases,

	@NotBlank(message = "[❎ ERROR] 정답 코드는 필수 입력값입니다.")
	String answer
) {
}
