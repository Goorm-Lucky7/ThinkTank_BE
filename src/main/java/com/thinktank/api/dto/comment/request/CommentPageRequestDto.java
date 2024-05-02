package com.thinktank.api.dto.comment.request;

public record CommentPageRequestDto(
	int pageIndex,
	boolean isDone
) {
}
