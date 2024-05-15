package com.thinktank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thinktank.api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	@Query("SELECT u.id FROM User u WHERE u.nickname = :nickname")
	Long findUserIdByNickname(@Param("nickname") String userNickname);

	Long findUserIdByEmail(String email);
}
