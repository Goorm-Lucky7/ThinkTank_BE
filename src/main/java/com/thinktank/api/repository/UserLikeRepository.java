package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.UserLike;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
}
