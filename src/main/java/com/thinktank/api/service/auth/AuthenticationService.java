package com.thinktank.api.service.auth;

import static com.thinktank.global.common.util.AuthConstants.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.auth.KakaoLoginReqDto;
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

	@Transactional
	public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {
		final User user = findByUserEmail(loginReqDto.email());
		validatePasswordMatch(loginReqDto.password(), user.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(user.getEmail());
		user.updateRefreshToken(refreshToken);

		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
		response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);

		return new LoginResDto(accessToken, refreshToken);
	}

	@Transactional
	public LoginResDto kakaoLogin(KakaoLoginReqDto kakaoLoginReqDto, HttpServletResponse response) {
		final User user = userRepository.findByEmail(kakaoLoginReqDto.email())
			.orElseGet(() -> registerUser(kakaoLoginReqDto));
		userProfileService.createProfileImage(user);

		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(user.getRefreshToken());
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

	private User registerUser(KakaoLoginReqDto kakaoLoginReqDto) {
		final User user = User.kakaoSignup(
			kakaoLoginReqDto, passwordEncoder.encode("SecureRandomPassword")
		);

		return userRepository.save(user);
	}
}
