package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Like;
import com.thinktank.api.entity.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByPost(Post post);
}
