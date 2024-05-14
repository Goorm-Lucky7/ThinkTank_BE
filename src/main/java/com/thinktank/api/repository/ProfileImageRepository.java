package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

	Optional<ProfileImage> findByUserEmail(String email);

	default String findByUserId(Long userId) {
		ProfileImage profileImage = findById(userId).orElse(null);
		return profileImage != null ? profileImage.getProfileImage() : null;
	}
}
