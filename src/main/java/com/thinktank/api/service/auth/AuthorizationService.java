package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.thinktank.api.entity.TokenSave;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.auth.TokenRepository;
import com.thinktank.global.auth.service.ClientDetails;
import com.thinktank.global.common.util.CookieUtils;
import com.thinktank.global.config.TokenConfig;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	private final JwtProviderService jwtProviderService;

	private final JwtAuthenticationService jwtAuthenticationService;

	private final TokenRepository tokenRepository;

	private final TokenConfig tokenConfig;

	public void addRefreshToken(String username, String refreshToken, long expirationTimeMs) {

		Date date = new Date(System.currentTimeMillis() + expirationTimeMs);

		TokenSave tokenSave = TokenSave.builder()
			.username(username)
			.refreshToken(refreshToken)
			.expiration(date.toString())
			.build();

		tokenRepository.save(tokenSave);
	}

	public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = CookieUtils.findCookieValue(request, REFRESH_TOKEN_COOKIE_NAME)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION));

		validateTokenNotExpired(refreshToken);
		validateTokenIsRefresh(refreshToken);

		String username = jwtProviderService.getUsernameFromToken(refreshToken);

		String newAccessToken = jwtProviderService.provideAccessToken(
			ACCESS_TOKEN_HEADER,
			username,
			tokenConfig.getAccessTokenExpirationTimeMs()
		);
		String newRefreshToken = jwtProviderService.provideAccessToken(
			REFRESH_TOKEN_COOKIE_NAME,
			username,
			tokenConfig.getRefreshTokenExpirationTimeMs()
		);

		tokenRepository.deleteByRefreshToken(refreshToken);
		addRefreshToken(username, refreshToken, tokenConfig.getRefreshTokenExpirationTimeMs());

		response.setHeader(ACCESS_TOKEN_HEADER, newAccessToken);
		response.addCookie(CookieUtils.tokenCookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken));
	}

	public Authentication getAuthentication(String token) {

		String username = jwtProviderService.getUsernameFromToken(token);
		User user = User.builder().email(username).build();
		ClientDetails clientDetails = new ClientDetails(user);

		return new UsernamePasswordAuthenticationToken(
			clientDetails,
			null,
			clientDetails.getAuthorities()
		);
	}

	private void validateTokenNotExpired(String refreshToken) {

		if (jwtAuthenticationService.isTokenExpired(refreshToken)) {
			throw new UnauthorizedException(ErrorCode.FAIL_TOKEN_EXPIRE_EXCEPTION);
		}
	}

	private void validateTokenIsRefresh(String refreshToken) {

		if (!jwtProviderService.getCategoryFromToken(refreshToken).equals(REFRESH_TOKEN_COOKIE_NAME)) {
			throw new UnauthorizedException(ErrorCode.FAIL_NOT_TOKEN_FOUND_EXCEPTION);
		}
	}
}
