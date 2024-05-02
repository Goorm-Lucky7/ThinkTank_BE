package com.thinktank.api.dto.comment.response;

import jakarta.validation.constraints.NotNull;

public record CommentResponseDto(
	@NotNull
	Long commentId,
	@NotNull
	String content,
	@NotNull
	String createdAt,
	@NotNull
	CommentUserResponseDto user
) {
}
