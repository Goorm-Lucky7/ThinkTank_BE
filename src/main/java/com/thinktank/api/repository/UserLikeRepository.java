package com.thinktank.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

	Optional<UserLike> findByLikeIdAndUserId(Long id, Long userId);

	boolean existsByLikeIdAndUserId(Long id, Long userId);

	UserLike findByUserIdAndLikePostId(Long userId, Long postId);

	List<UserLike> findByLikePostId(Long postId);

	List<UserLike> findByUserAndIsCheckTrue(User user);
}
