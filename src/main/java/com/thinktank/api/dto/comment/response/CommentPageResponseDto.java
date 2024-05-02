package com.thinktank.api.dto.comment.response;

import jakarta.annotation.Nullable;

public record CommentPageResponseDto(
	int pageIndex,
	boolean isDone
) {
}
