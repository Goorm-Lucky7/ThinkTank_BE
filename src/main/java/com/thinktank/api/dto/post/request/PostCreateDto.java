package com.thinktank.api.dto.post.request;

import org.hibernate.validator.constraints.Length;

import com.thinktank.api.entity.Category;
import com.thinktank.api.entity.Language;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public record PostCreateDto(
	@NotBlank @Length(max = 20,message = "제목은 최대 20자 입니다.")
	String title,
	@NotBlank(message = "카테고리는 필수 입력값입니다.")
	Category category,
	@NotBlank(message = "언어는 필수 입력값입니다")
	Language language,
	@NotBlank(message = "문제 설명은 필수 입력값입니다.")
	String content,
	@NotBlank(message = "제약 조건은 필수 입력값입니다.")
	String condition,
	@NotBlank(message = "정답 코드는 필수 입력값입니다.")
	String answer

) {
}
