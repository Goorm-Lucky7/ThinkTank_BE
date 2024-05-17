package com.thinktank.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

	Optional<UserLike> findByLikeIdAndUserId(Long id, Long userId);

	boolean existsByLikeIdAndUserId(Long id, Long userId);

	UserLike findByUserEmailAndLikePostId(String email, Long postId);

	List<UserLike> findByLikePost(Post post);

	Page<UserLike> findByUserAndIsCheckTrue(User user, Pageable pageable);
}
