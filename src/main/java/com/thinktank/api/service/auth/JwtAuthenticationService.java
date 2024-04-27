package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.thinktank.global.config.TokenConfig;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

	private final TokenConfig tokenConfig;

	private final JwtProviderService jwtProviderService;

	public boolean isTokenExpired(String token) {

		return Jwts.parser()
			.verifyWith(tokenConfig.getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public void validateToken(String token, HttpServletResponse response) throws IOException {

		try {
			if (isTokenExpired(token)) {
				throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRE_EXCEPTION);
			}

			if (!isTokenValidCategory(token)) {
				throw new UnauthorizedException(ErrorCode.FAIL_INVALID_TOKEN_CATEGORY);
			}
		} catch (ExpiredJwtException e) {
			log.error("TOKEN EXPIRED: {}", e.getMessage());
			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRE_EXCEPTION);
		} catch (Exception e) {
			log.error("TOKEN VALIDATION ERROR: {}", e.getMessage());
			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_VALIDATION);
		}
	}

	private boolean isTokenValidCategory(String token) {
		String category = jwtProviderService.getCategoryFromToken(token);
		return category.equals(ACCESS_TOKEN_HEADER);
	}
}
