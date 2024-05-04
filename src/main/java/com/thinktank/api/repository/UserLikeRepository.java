package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.UserLike;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

	Optional<UserLike> findByLikeIdAndUserId(Long id, Long userId);

	boolean existsByLikeIdAndUserId(Long id, Long userId);

	UserLike findByUserIdAndLikePostId(Long userId, Long postId);
}
