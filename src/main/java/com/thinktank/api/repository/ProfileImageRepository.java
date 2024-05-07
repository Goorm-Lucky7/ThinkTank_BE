package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

	Optional<ProfileImage> findByUserEmail(String email);
}
