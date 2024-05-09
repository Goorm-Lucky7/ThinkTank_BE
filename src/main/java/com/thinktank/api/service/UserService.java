package com.thinktank.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.profileImage.response.ProfileImageResDto;
import com.thinktank.api.dto.user.request.SignUpDto;
import com.thinktank.api.dto.user.request.UserDeleteDto;
import com.thinktank.api.dto.user.request.UserUpdateDto;
import com.thinktank.api.dto.user.response.UserResDto;
import com.thinktank.api.entity.ProfileImage;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.ProfileImageRepository;
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
	private final ProfileImageRepository profileImageRepository;

	private final UserProfileService userProfileService;

	@Transactional
	public void signUp(SignUpDto signUpDto) {
		validateEmailNotExists(signUpDto.email());
		validateNicknameNotExists(signUpDto.nickname());
		validatePasswordEquality(signUpDto.password(), signUpDto.checkPassword());

		final String encodedPassword = passwordEncoder.encode(signUpDto.password());
		final User user = User.signup(signUpDto, encodedPassword);

		userRepository.save(user);

		userProfileService.createProfileImage(user);
	}

	public UserResDto getOwnProfileDetails(AuthUser authUser) {
		final User user = findByUserEmail(authUser.email());

		final ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_IMAGE_EXCEPTION));

		return convertToUserResDto(user, profileImage);
	}

	@Transactional
	public void updateUserNickname(AuthUser authUser, UserUpdateDto userUpdateDto) {
		final User user = findByUserEmail(authUser.email());

		validateNicknameNotExists(userUpdateDto.nickname());

		user.updateNickname(userUpdateDto.nickname());
	}

	@Transactional
	public void removeUser(AuthUser authUser, UserDeleteDto userDeleteDto) {
		final User user = findByUserEmail(authUser.email());
		validatePasswordMatch(userDeleteDto.password(), user.getPassword());

		final ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_IMAGE_EXCEPTION));

		profileImageRepository.delete(profileImage);
		userRepository.delete(user);
	}

	private UserResDto convertToUserResDto(User user, ProfileImage profileImage) {
		return new UserResDto(user.getEmail(), user.getNickname(), user.getGithub(), user.getBlog(),
			user.getIntroduce(), convertToProfileImageResDto(profileImage));
	}

	private ProfileImageResDto convertToProfileImageResDto(ProfileImage profileImage) {
		return new ProfileImageResDto(profileImage.getFileName(), profileImage.getFileUrl(),
			profileImage.getOriginalFileName());
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

	private void validatePasswordEquality(String rawPassword, String checkPassword) {
		if (!rawPassword.equals(checkPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}

	private void validatePasswordMatch(String rawPassword, String encodedPassword) {
		if (passwordEncoder.matches(encodedPassword, rawPassword)) {
			throw new BadRequestException(ErrorCode.FAIL_WRONG_PASSWORD);
		}
	}
}
