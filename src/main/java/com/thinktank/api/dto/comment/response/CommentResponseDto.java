package com.thinktank.api.dto.comment.response;

public record CommentResponseDto(
	Long commentId,
	String content,
	String createdAt,
	CommentUserResponseDto user
) {
}
