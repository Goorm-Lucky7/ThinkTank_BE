package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thinktank.api.entity.Like;
import com.thinktank.api.entity.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByPost(Post post);

	@Query("SELECT COALESCE(SUM(l.likeCount), 0) FROM Like l WHERE l.post = :post")
	int findLikeCountByPost(@Param("post") Post post);

	void deleteByPost(Post post);
}