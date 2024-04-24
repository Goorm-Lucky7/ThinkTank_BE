package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
