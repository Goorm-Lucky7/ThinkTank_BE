package com.thinktank.api.dto.user.request;

import jakarta.annotation.Nullable;

public record UserIdReqDto(
	@Nullable
	Long userId
) {
}
