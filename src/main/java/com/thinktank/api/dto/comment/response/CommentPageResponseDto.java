package com.thinktank.api.dto.comment.response;

public record CommentPageResponseDto(
	int pageIndex,
	boolean isDone
) {
}
