package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.auth.GoogleOAuthTokenDto;
import com.thinktank.api.dto.auth.GoogleUserInfoResDto;
import com.thinktank.api.dto.auth.KakaoOAuthTokenDto;
import com.thinktank.api.dto.auth.KakaoUserInfoResDto;
import com.thinktank.api.dto.auth.OAuthLoginReqDto;
import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.response.LoginResDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.api.service.UserProfileService;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProviderService jwtProviderService;
	private final UserProfileService userProfileService;
	private final OAuth2AuthorizationService oAuth2AuthorizationService;

	@Transactional
	public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {
		final User user = findByUserEmail(loginReqDto.email());
		validatePasswordMatch(loginReqDto.password(), user.getPassword());

		return processUserAuthentication(user, response);
	}

	@Transactional
	public LoginResDto socialLogin(OAuthLoginReqDto OAuthLoginReqDto, HttpServletResponse response) {
		final User user = userRepository.findByEmail(OAuthLoginReqDto.email())
			.orElseGet(() -> registerUser(OAuthLoginReqDto));

		userProfileService.createProfileImage(user);

		return processUserAuthentication(user, response);
	}

	public void redirectToKakaoLoginPage(HttpServletResponse response) {
		String kakaoUri = oAuth2AuthorizationService.responseKakaoUri();
		redirectToUri(response, kakaoUri);
	}

	public void redirectToGoogleLoginPage(HttpServletResponse response) {
		String googleUri = oAuth2AuthorizationService.responseGoogleUri();
		redirectToUri(response, googleUri);
	}

	public KakaoUserInfoResDto kakaoLogin(String code) {
		ResponseEntity<String> accessToken = oAuth2AuthorizationService.requestKakaoAccessToken(code);
		KakaoOAuthTokenDto kakaoOAuthTokenDto = oAuth2AuthorizationService.getKakaoAccessToken(accessToken);

		ResponseEntity<String> userInfoResponse = oAuth2AuthorizationService.requestKakaoUserInfo(kakaoOAuthTokenDto);

		return oAuth2AuthorizationService.getKakaoUserInfo(userInfoResponse);
	}

	public GoogleUserInfoResDto googleLogin(String code) {
		ResponseEntity<String> accessToken = oAuth2AuthorizationService.requestGoogleAccessToken(code);
		GoogleOAuthTokenDto googleOAuthTokenDto = oAuth2AuthorizationService.getGoogleAccessToken(accessToken);

		ResponseEntity<String> userInfoResponse = oAuth2AuthorizationService.requestGoogleUserInfo(googleOAuthTokenDto);

		return oAuth2AuthorizationService.getGoogleUserInfo(userInfoResponse);
	}

	private void redirectToUri(HttpServletResponse response, String uri) {
		try {
			response.sendRedirect(uri);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private LoginResDto processUserAuthentication(User user, HttpServletResponse response) {
		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(user.getEmail());
		user.updateRefreshToken(refreshToken);

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
		response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);

		return new LoginResDto(accessToken, refreshToken);
	}

	private User findByUserEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
	}

	private void validatePasswordMatch(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}

	private User registerUser(OAuthLoginReqDto OAuthLoginReqDto) {
		final User user = User.kakaoSignup(
			OAuthLoginReqDto, passwordEncoder.encode("SecureRandomPassword")
		);

		return userRepository.save(user);
	}
}
