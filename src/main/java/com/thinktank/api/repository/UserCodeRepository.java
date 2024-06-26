package com.thinktank.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserCode;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
	boolean existsByUserAndPost(User user, Post post);

	@Query("SELECT COUNT(uc) FROM UserCode uc WHERE uc.post = :post")
	int countUserCodeByPost(@Param("post") Post post);

	void deleteByPost(Post post);

	UserCode findByPostAndUser(Post post, User user);

	Page<UserCode> findByUser(User user, Pageable pageable);
}
