package com.thinktank.api.dto.post.request;

import jakarta.validation.constraints.NotNull;

public record PostDeleteDto(
	@NotNull
	Long postId
) {
}
