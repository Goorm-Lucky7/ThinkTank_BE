package com.thinktank.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thinktank.api.entity.Comment;
import com.thinktank.api.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC ")
	Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);

	@Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
	int countCommentsByPost(@Param("post") Post post);

	void deleteByPost(Post post);
}

