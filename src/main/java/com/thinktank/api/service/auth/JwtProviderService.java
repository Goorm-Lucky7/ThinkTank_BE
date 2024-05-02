package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.common.util.CookieUtils;
import com.thinktank.global.config.TokenConfig;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
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

	private final TokenConfig tokenConfig;
	private final UserRepository userRepository;

	public String generateAccessToken(String email, String nickname) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + tokenConfig.getAccessTokenExpire());

		return buildJwt(issuedDate, expiredDate)
			.claim(EMAIL, email)
			.claim(NICKNAME, nickname)
			.compact();
	}

	public String generateRefreshToken(String email) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + tokenConfig.getRefreshTokenExpire());

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

		Cookie refreshTokenCookie = CookieUtils.generateRefreshTokenCookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
		response.addCookie(refreshTokenCookie);

		return newAccessToken;
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

	public String extractRefreshToken(String cookieName, HttpServletRequest request) {
		String refreshToken = CookieUtils.getCookieValue(cookieName, request);

		if (refreshToken == null) {
			log.warn("{} COOKIE NOT FOUND", cookieName);
			throw new NotFoundException(ErrorCode.FAIL_NOT_COOKIE_FOUND_EXCEPTION);
		}

		return refreshToken;
	}

	public AuthUser extractAuthUserByAccessToken(String token) {
		final Claims claims = getClaimsByToken(token);
		final String email = claims.get(EMAIL, String.class);
		final String nickname = claims.get(NICKNAME, String.class);

		return AuthUser.create(email, nickname);
	}

	public boolean isUsable(String token, HttpServletResponse response) {
		try {
			Jwts.parser()
				.verifyWith(tokenConfig.getSecretKey())
				.build()
				.parseSignedClaims(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("TOKEN EXPIRED");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			throw new NotFoundException(ErrorCode.FAIL_TOKEN_EXPIRED_EXCEPTION);
		} catch (IllegalArgumentException e) {
			log.warn("TOKEN IS NULL");
			throw new NotFoundException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION);
		} catch (Exception e) {
			log.warn("INVALID TOKEN");
			throw new NotFoundException(ErrorCode.FAIL_INVALID_TOKEN_EXCEPTION);
		}
	}

	private JwtBuilder buildJwt(Date issuedDate, Date expiredDate) {
		return Jwts.builder()
			.issuedAt(issuedDate)
			.expiration(expiredDate)
			.signWith(tokenConfig.getSecretKey(), Jwts.SIG.HS256)
			.setHeaderParam("alg", "HS256")
			.setHeaderParam("typ", "JWT");
	}

	private Claims getClaimsByToken(String token) {
		return Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private void validateRefreshToken(String currentRefreshToken, String savedRefreshToken) {
		if (!currentRefreshToken.equals(savedRefreshToken)) {
			log.warn("INVALID REFRESH TOKEN");
			throw new NotFoundException(ErrorCode.FAIL_INVALID_TOKEN_EXCEPTION);
		}
	}
}
