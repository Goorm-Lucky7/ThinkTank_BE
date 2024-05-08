package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thinktank.api.entity.auth.AuthUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProviderService {

	private static final String EMAIL = "email";
	private static final String NICKNAME = "nickname";

	@Value("${jwt.secret.access-key}")
	private String secret;

	@Value("${jwt.access-expire}")
	private long accessTokenExpire;

	private SecretKey secretKey;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(String email, String nickname) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpire);

		return buildJwt(issuedDate, expiredDate).claim(EMAIL, email).claim(NICKNAME, nickname).compact();
	}

	public boolean isTokenExpired(String token) {
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String reGenerateExpiredAccessToken(String expiredToken) {
		final Claims claims = getClaimsByToken(expiredToken);
		final String email = claims.get(EMAIL, String.class);
		final String nickname = claims.get(NICKNAME, String.class);

		return generateAccessToken(email, nickname);
	}

	public String extractAccessToken(String header, HttpServletRequest request) {
		String token = request.getHeader(header);

		if (token == null) {
			log.warn("{} IS NULL", header);
			return null;
		} else if (!token.startsWith(BEARER)) {
			log.warn("{} IS NOT BEARER", header);
			return null;
		}

		return token.replaceFirst(BEARER, "").trim();
	}

	public AuthUser extractAuthUserByAccessToken(String token) {
		final Claims claims = getClaimsByToken(token);
		final String email = claims.get(EMAIL, String.class);
		final String nickname = claims.get(NICKNAME, String.class);

		return AuthUser.create(email, nickname);
	}

	public boolean isUsable(String token) {
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("{} TOKEN EXPIRED", token);
			return false;
		} catch (Exception e) {
			log.warn("INVALID {} TOKEN", token);
			return false;
		}
	}

	private JwtBuilder buildJwt(Date issuedDate, Date expiredDate) {
		return Jwts.builder()
			.issuedAt(issuedDate)
			.expiration(expiredDate)
			.signWith(secretKey, Jwts.SIG.HS256)
			.setHeaderParam("alg", "HS256")
			.setHeaderParam("typ", "JWT");
	}

	private Claims getClaimsByToken(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}
}
