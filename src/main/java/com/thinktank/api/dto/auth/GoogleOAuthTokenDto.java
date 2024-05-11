package com.thinktank.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleOAuthTokenDto(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("expires_in") String expiresIn,
	@JsonProperty("scope") String scope,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("id_token") String idToken
) {
}
