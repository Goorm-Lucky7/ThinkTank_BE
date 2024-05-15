package com.thinktank.api.dto.comment.response;

import jakarta.validation.constraints.NotNull;

public record CommentResDto(
	@NotNull
	Long commentId,
	@NotNull
	String content,
	@NotNull
	String createdAt,
	boolean isAuthor,
	@NotNull
	CommentUserResDto user
) {
}
