package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserCode;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
	Boolean existsByUserAndPost(User user, Post post);

	@Query("SELECT COUNT(uc) FROM UserCode uc WHERE uc.post = :post")
	int countUserCodeByPost(@Param("post") Post post);

	void deleteByPostId(Long postId);
}
