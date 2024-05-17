package com.thinktank.api.dto.comment.response;

import jakarta.validation.constraints.NotNull;

public record CommentUserResDto(
	
	@NotNull
	String nickname,

	@NotNull
	String profileImage

) {
}
