package com.thinktank.api.service;

import static com.thinktank.global.common.util.AuthConstants.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.response.LoginResDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.common.util.CookieUtils;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProviderService jwtProviderService;

	@Transactional
	public void signUp(SignUpDto signUpDto) {
		validateEmailNotExists(signUpDto.email());
		validateNicknameNotExists(signUpDto.nickname());
		validatePasswordEquality(signUpDto.password(), signUpDto.checkPassword());

		final String encodedPassword = passwordEncoder.encode(signUpDto.password());
		final User user = User.signup(signUpDto, encodedPassword);

		userRepository.save(user);
	}

	@Transactional
	public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {
		final User user = userRepository.findByEmail(loginReqDto.email())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		validatePasswordMatch(loginReqDto.password(), user.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(user.getEmail());
		user.updateRefreshToken(refreshToken);

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);

		Cookie refreshTokenCookie = CookieUtils.generateRefreshTokenCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
		response.addCookie(refreshTokenCookie);

		return new LoginResDto(accessToken, refreshToken);
	}

	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtProviderService.extractRefreshToken(REFRESH_TOKEN_COOKIE_NAME, request);

		AuthUser authUser = jwtProviderService.extractAuthUserByAccessToken(refreshToken);
		if (authUser != null) {
			final User user = userRepository.findByEmail(authUser.email())
				.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

			user.updateRefreshToken(null);
		}

		Cookie refreshTokenCookie = CookieUtils.expireRefreshTokenCookie(REFRESH_TOKEN_COOKIE_NAME);

		response.addCookie(refreshTokenCookie);
	}

	private void validateEmailNotExists(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BadRequestException(ErrorCode.FAIL_EMAIL_CONFLICT);
		}
	}

	private void validateNicknameNotExists(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new BadRequestException(ErrorCode.FAIL_NICKNAME_CONFLICT);
		}
	}

	private void validatePasswordEquality(String password, String checkPassword) {
		if (!password.equals(checkPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}

	private void validatePasswordMatch(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}
}
