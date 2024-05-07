package com.thinktank.api.dto.comment.response;

import java.util.List;

import com.thinktank.api.dto.page.response.PageInfoDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CommentsResponseDto(
	@NotNull
	Long postId,
	@Nullable
	List<CommentResponseDto> comments,
	@NotNull
	PageInfoDto pageInfoDto
) {
}
