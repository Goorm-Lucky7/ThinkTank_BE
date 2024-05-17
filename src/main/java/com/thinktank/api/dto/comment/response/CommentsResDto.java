package com.thinktank.api.dto.comment.response;

import java.util.List;

import com.thinktank.api.dto.page.response.PageInfo;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CommentsResDto(
	@NotNull
	Long postId,
	@Nullable
	List<CommentResDto> comments,
	@NotNull
	PageInfo pageInfo
) {
}
