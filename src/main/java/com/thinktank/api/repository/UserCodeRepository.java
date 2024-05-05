package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserCode;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
	Boolean existsByUserAndPost(User user, Post post);
}
