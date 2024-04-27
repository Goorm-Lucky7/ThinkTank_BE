package com.thinktank.global.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "spring.jwt")
public class TokenConfig {

	private final String secret;

	private final long accessTokenExpirationTimeMs;

	private final long refreshTokenExpirationTimeMs;

	private final SecretKey secretKey;

	public TokenConfig(String secret, long accessTokenExpirationTimeMs, long refreshTokenExpirationTimeMs) {
		this.secret = secret;
		this.accessTokenExpirationTimeMs = accessTokenExpirationTimeMs;
		this.refreshTokenExpirationTimeMs = refreshTokenExpirationTimeMs;
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
