package com.thinktank.api.dto.comment.response;

import jakarta.validation.constraints.NotNull;

public record CommentUserResponseDto(
	@NotNull
	String nickname
	// String profileImage
) {
}
