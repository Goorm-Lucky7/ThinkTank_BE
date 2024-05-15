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
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

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
	private static final String PROFILE_IMAGE_URL = "profileImageUrl";

	@Value("${jwt.secret.access-key}")
	private String secret;

	@Value("${jwt.access-expire}")
	private long accessTokenExpire;

	private SecretKey secretKey;

	private final UserRepository userRepository;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String email, String nickname) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(EMAIL, email)
			.claim(NICKNAME, nickname)
			.compact();
	}

	public String generateSocialToken(String email, String nickname, String profileImageUrl) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(EMAIL, email)
			.claim(NICKNAME, nickname)
			.claim(PROFILE_IMAGE_URL, profileImageUrl)
			.compact();
	}

	@Transactional
	public String reGenerateToken(String accessToken) {
		final Claims claims = getClaimsByToken(accessToken);
		final String email = claims.get(EMAIL, String.class);
		final User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_USER_NOT_FOUND));

		return generateToken(user.getEmail(), user.getNickname());
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
		} catch (Exception e) {
			log.warn("====== INVALID TOKEN ======");
			throw new UnauthorizedException(ErrorCode.FAIL_INVALID_TOKEN);
		}

		return false;
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
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		} catch (Exception e) {
			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRED);
		}
	}
}
