package com.thinktank.api.dto;

import lombok.Builder;

@Builder
public record TokenResDto(
	String accessToken
) {
}
