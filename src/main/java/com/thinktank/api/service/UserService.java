package com.thinktank.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.LoginReqDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.response.LoginResDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

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
	public LoginResDto login(LoginReqDto loginReqDto) {
		final User user = userRepository.findByEmail(loginReqDto.email())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		validatePasswordMatch(loginReqDto.password(), user.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(user.getEmail(), user.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(user.getEmail());
		user.updateRefreshToken(refreshToken);

		return new LoginResDto(accessToken, refreshToken);
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
