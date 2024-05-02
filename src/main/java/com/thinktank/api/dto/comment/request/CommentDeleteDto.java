package com.thinktank.api.dto.comment.request;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteDto(
	@NotNull
	long postId,
	@NotNull
	long commentId
) {
}
