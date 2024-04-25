package com.thinktank.api.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.entity.TokenSave;

@Repository
public interface TokenRepository extends JpaRepository<TokenSave, Long> {

	Boolean existsByRefreshToken(String refreshToken);

	@Transactional
	void deleteByRefreshToken(String refresh);
}
