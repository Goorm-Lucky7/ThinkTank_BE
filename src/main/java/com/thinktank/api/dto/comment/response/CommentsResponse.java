package com.thinktank.api.dto.comment.response;

import java.util.List;

import com.thinktank.api.dto.comment.request.CommentPageRequestDto;

public record CommentsResponse(
	Long postId,
	List<CommentResponseDto> comments,
	CommentPageRequestDto pageRequestDto
) {
}
