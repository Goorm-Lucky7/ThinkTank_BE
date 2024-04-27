package com.thinktank.api.dto.comment;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
	@NotBlank(message = "댓글은 공백으로 작성하실 수 없습니다.")
	@Length(max = CONTENT_MAX_LENGTH, message = "댓글은 100자 이내로 작성하셔야 합니다.")
	String content) {

	private static final int CONTENT_MAX_LENGTH = 100;
}
