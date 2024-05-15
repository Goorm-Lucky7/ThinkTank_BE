package com.thinktank.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findByUser(User user, Pageable pageable);
}
