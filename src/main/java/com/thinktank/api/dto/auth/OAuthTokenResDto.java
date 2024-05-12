package com.thinktank.api.dto.auth;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthTokenResDto(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("expires_in") String expiresIn,
	@JsonProperty("scope") String scope,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("id_token") Optional<String> idToken,
	@JsonProperty("refresh_token") Optional<String> refreshToken,
	@JsonProperty("refresh_token_expires_in") Optional<String> refreshTokenExpiresIn
) {
}
