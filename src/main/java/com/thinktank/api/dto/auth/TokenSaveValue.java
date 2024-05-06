package com.thinktank.api.dto.auth;

import lombok.Builder;

@Builder
public record TokenSaveValue(
	String refreshToken
) {
}
