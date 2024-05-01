package com.thinktank.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.response.UserResDto;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.UserRepository;
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

	@Transactional
	public void signUp(SignUpDto signUpDto) {
		validateEmailNotExists(signUpDto.email());
		validateNicknameNotExists(signUpDto.nickname());
		validatePasswordEquality(signUpDto.password(), signUpDto.checkPassword());

		final String encodedPassword = passwordEncoder.encode(signUpDto.password());
		final User user = User.signup(signUpDto, encodedPassword);

		userRepository.save(user);
	}

	public UserResDto findUserDetails(AuthUser authUser) {
		final User user = findByUserEmail(authUser.email());
		return convertToUserResDto(user);
	}

	private UserResDto convertToUserResDto(User user) {
		return new UserResDto(user.getEmail(), user.getNickname(), user.getGithub(), user.getBlog(),
			user.getIntroduce());
	}

	private User findByUserEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
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
}
