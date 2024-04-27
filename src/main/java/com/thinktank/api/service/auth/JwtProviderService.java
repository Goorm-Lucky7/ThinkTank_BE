package com.thinktank.api.service.auth;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.thinktank.global.config.TokenConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtProviderService {

	private final TokenConfig tokenConfig;

	public String getUsernameFromToken(String token) {

		return parseClaims(token).get("username", String.class);
	}

	public String getCategoryFromToken(String token) {

		return parseClaims(token).get("category", String.class);
	}

	public String provideAccessToken(String category, String username, long accessExpireMs) {

		return commonInfo(category, username, accessExpireMs);
	}

	public String provideRefreshToken(String category, String username, long refreshExpireMs) {

		return commonInfo(category, username, refreshExpireMs);
	}

	private String commonInfo(String category, String username, long expireMs) {

		Date issuDate = new Date();
		Date expireDate = new Date(issuDate.getTime() + expireMs);

		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.issuedAt(issuDate)
			.expiration(expireDate)
			.signWith(tokenConfig.getSecretKey())
			.compact();
	}

	private Claims parseClaims(String token) {

		return Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
