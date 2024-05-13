package com.thinktank.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.request.UserUpdateDto;
import com.thinktank.api.dto.user.response.UserProfileResDto;
import com.thinktank.api.entity.ProfileImage;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.ProfileImageRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProfileImageRepository profileImageRepository;

	@Transactional
	public void signUp(SignUpDto signUpDto) {
		validateEmailNotExists(signUpDto.email());
		validateNicknameNotExists(signUpDto.nickname());
		validatePasswordEquality(signUpDto.password(), signUpDto.checkPassword());

		final String encodedPassword = passwordEncoder.encode(signUpDto.password());
		final User user = User.signup(signUpDto, encodedPassword);
		final ProfileImage profileImage = ProfileImage.createDefaultForUser(user);

		userRepository.save(user);
		profileImageRepository.save(profileImage);
	}

	public UserProfileResDto getOwnProfileDetails(AuthUser authUser) {
		final User user = findUserByEmail(authUser.email());
		final ProfileImage profileImage = findProfileImageByEmail(user.getEmail());

		return convertToUserProfileResDto(user, profileImage);
	}

	@Transactional
	public void updateUserDetails(AuthUser authUser, UserUpdateDto userUpdateDto) {
		final User user = findUserByEmail(authUser.email());
		validateNicknameNotExists(userUpdateDto.nickname());

		final ProfileImage profileImage = findProfileImageByEmail(user.getEmail());

		user.updateUserProfile(userUpdateDto);
		profileImage.updateProfileImage(userUpdateDto);
	}

	@Transactional
	public void removeUser(AuthUser authUser) {
		final User user = findUserByEmail(authUser.email());

		final ProfileImage profileImage = findProfileImageByEmail(user.getEmail());

		userRepository.delete(user);
		profileImageRepository.delete(profileImage);
	}

	private UserProfileResDto convertToUserProfileResDto(User user, ProfileImage profileImage) {
		return new UserProfileResDto(user.getEmail(), user.getNickname(), user.getGithub(), user.getBlog(),
			user.getIntroduce(), profileImage.getProfileImage());
	}

	private ProfileImage findProfileImageByEmail(String email) {
		return profileImageRepository.findByUserEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_IMAGE_NOT_FOUND));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
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

	private void validatePasswordEquality(String rawPassword, String checkPassword) {
		if (!rawPassword.equals(checkPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_INCORRECT_PASSWORD);
		}
	}
}
