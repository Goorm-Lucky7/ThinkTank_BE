package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
