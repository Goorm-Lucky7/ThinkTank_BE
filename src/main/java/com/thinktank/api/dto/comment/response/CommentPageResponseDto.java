package com.thinktank.api.dto.comment.response;

import jakarta.annotation.Nullable;

public record CommentPageResponseDto(
	@Nullable
	int pageIndex,
	@Nullable
	boolean isDone
) {
}
