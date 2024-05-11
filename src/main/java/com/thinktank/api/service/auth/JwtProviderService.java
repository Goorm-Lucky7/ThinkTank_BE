package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

	@Value("${jwt.refresh-expire}")
	private long refreshTokenExpire;

	private SecretKey secretKey;

	private final UserRepository userRepository;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(String email, String nickname) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(EMAIL, email)
			.claim(NICKNAME, nickname)
			.compact();
	}

	public String generateRefreshToken(String email) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + refreshTokenExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(EMAIL, email)
			.compact();
	}

	@Transactional
	public String reGenerateToken(String refreshToken, HttpServletResponse response) {
		final Claims claims = getClaimsByToken(refreshToken);
		final String email = claims.get(EMAIL, String.class);
		final User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		validateRefreshToken(refreshToken, user.getRefreshToken());

		final String newAccessToken = generateAccessToken(user.getEmail(), user.getNickname());
		final String newRefreshToken = generateRefreshToken(user.getEmail());

		user.updateRefreshToken(newRefreshToken);

		response.setHeader(ACCESS_TOKEN_HEADER, newAccessToken);
		response.setHeader(REFRESH_TOKEN_HEADER, newRefreshToken);

		return newAccessToken;
	}

	public String extractToken(String header, HttpServletRequest request) {
		String token = request.getHeader(header);

		if (token == null || !token.startsWith(BEARER)) {
			log.warn("====== {} is null or not bearer =======", header);
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
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("====== TOKEN EXPIRED ======");
		} catch (IllegalArgumentException e) {
			log.warn("====== EMPTIED TOKEN ======");
			throw new NotFoundException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION);
		} catch (Exception e) {
			log.warn("====== INVALID TOKEN ======");
			throw new NotFoundException(ErrorCode.FAIL_INVALID_TOKEN_EXCEPTION);
		}

		return false;
	}

	private void validateRefreshToken(String currentRefreshToken, String savedRefreshToken) {
		if (!currentRefreshToken.equals(savedRefreshToken)) {
			log.warn("===== INVALID REFRESH TOKEN =====");
			throw new NotFoundException(ErrorCode.FAIL_INVALID_TOKEN_EXCEPTION);
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
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
