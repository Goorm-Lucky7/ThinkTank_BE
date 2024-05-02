package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.profileImage.request.ProfileImageReqDto;
import com.thinktank.api.entity.ProfileImage;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.ProfileImageRepository;
import com.thinktank.api.repository.UserRepository;
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
	public void saveOrUpdateProfileImage(AuthUser authUser, ProfileImageReqDto profileImageReqDto) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));

		profileImageRepository.findByUserEmail(user.getEmail())
			.ifPresentOrElse(
				updateProfileImage -> updateProfileImage.updateProfileImage(profileImageReqDto, user),
				() -> createProfileImage(user)
			);
	}

	private void createProfileImage(User user) {
		ProfileImage profileImage = ProfileImage.createWithUser(user);
		profileImageRepository.save(profileImage);
	}
}
