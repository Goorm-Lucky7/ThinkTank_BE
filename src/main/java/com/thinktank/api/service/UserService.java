package com.thinktank.api.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.SignupDTO;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public void signup(SignupDTO signupDTO) {

		validateEmailNotExists(signupDTO.email());
		validateNicknameNotExists(signupDTO.nickname());
		validatePasswordMatch(signupDTO.password(), signupDTO.checkPassword());

		User user = User.signup(signupDTO, bCryptPasswordEncoder.encode(signupDTO.password()));

		userRepository.save(user);
	}

	private void validateEmailNotExists(String email) {

		if (userRepository.existsByEmail(email)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private void validateNicknameNotExists(String nickname) {

		if (userRepository.existsByNickname(nickname)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private void validatePasswordMatch(String password, String checkPassword) {

		if (!password.equals(checkPassword)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}
}
