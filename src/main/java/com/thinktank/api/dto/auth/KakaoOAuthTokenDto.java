package com.thinktank.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoOAuthTokenDto(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty("expires_in") String expiresIn,
	@JsonProperty("refresh_token_expires_in") String refreshTokenExpiresIn,
	@JsonProperty("scope") String scope,
	@JsonProperty("token_type") String tokenType
	) {
}
