package com.thinktank.api.dto.like.request;

import jakarta.validation.constraints.NotNull;

public record LikeCreateDto(
	@NotNull
	Long postId) {
}