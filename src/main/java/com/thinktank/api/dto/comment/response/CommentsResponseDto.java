package com.thinktank.api.dto.comment.response;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CommentsResponseDto(
	@NotNull
	Long postId,
	@Nullable
	List<CommentResponseDto> comments,
	@NotNull
	CommentPageResponseDto pageRequestDto
) {
}
