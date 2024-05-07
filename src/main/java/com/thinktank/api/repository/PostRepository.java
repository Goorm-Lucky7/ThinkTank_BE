package com.thinktank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByUser(User user);
}
