package com.thinktank.api.dto.auth;

import lombok.Builder;

@Builder
public record TokenResDto(
	String accessToken
) {
}
