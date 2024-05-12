package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.profileImage.request.ProfileImageReqDto;
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
public class UserProfileService {

	private final ProfileImageRepository profileImageRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createProfileImage(User user) {
		final ProfileImage profileImage = ProfileImage.createDefaultForUser(user);
		profileImageRepository.save(profileImage);
	}

	@Transactional
	public void saveOrUpdateSocialProfileImage(User user, String imageUrl) {
		ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElse(ProfileImage.createDefaultForUser(user));

		String fileName = extractFileNameFromUrl(imageUrl);

		ProfileImageReqDto profileImageReqDto = new ProfileImageReqDto(fileName, imageUrl, fileName);
		profileImage.updateProfileImage(profileImageReqDto);
	}

	@Transactional
	public void updateProfileImage(AuthUser authUser, ProfileImageReqDto profileImageReqDto) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		final ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_IMAGE_EXCEPTION));

		profileImage.updateProfileImage(profileImageReqDto);
	}

	@Transactional
	public void resetProfileImageToDefault(AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		final ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_IMAGE_EXCEPTION));

		validateDefaultImage(profileImage);

		profileImage.updateProfileImageToDefault();
	}

	private String extractFileNameFromUrl(String imageUrl) {
		return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
	}

	private void validateDefaultImage(ProfileImage profileImage) {
		if (DEFAULT_FILE_URL.equals(profileImage.getFileUrl())) {
			throw new BadRequestException(ErrorCode.FAIL_ALREADY_DEFAULT_IMAGE);
		}
	}
}
