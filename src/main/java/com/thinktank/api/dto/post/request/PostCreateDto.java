package com.thinktank.api.dto.post.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

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

	@NotBlank(message = "[❎ ERROR] 정답 코드는 필수 입력값입니다.")
	String answer
) {
}
