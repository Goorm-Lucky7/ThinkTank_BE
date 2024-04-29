package com.thinktank.global.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class TokenConfig {

	@Value("${jwt.secret.access-key}")
	private String secret;

	@Value("${jwt.access-expire}")
	private long accessTokenExpire;

	@Value("${jwt.refresh-expire}")
	private long refreshTokenExpire;

	private SecretKey secretKey;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
