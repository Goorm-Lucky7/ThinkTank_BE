package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
