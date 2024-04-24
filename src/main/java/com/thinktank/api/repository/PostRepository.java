package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
