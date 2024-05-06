package com.thinktank.api.repository.redis;

import java.time.Duration;

import org.springframework.stereotype.Repository;

import com.thinktank.api.dto.auth.TokenSaveValue;

@Repository
public class TokenRepository {

	private static final int EXPIRE_DAYS = 14;

	private final HashRedisRepository hashRedisRepository;

	public TokenRepository(HashRedisRepository hashRedisRepository) {
		this.hashRedisRepository = hashRedisRepository;
	}

	public void saveToken(String email, TokenSaveValue tokenSaveValue) {
		hashRedisRepository.save(email, tokenSaveValue, Duration.ofDays(EXPIRE_DAYS));
	}

	public TokenSaveValue getTokenSaveValue(String email) {
		return (TokenSaveValue)hashRedisRepository.get(email);
	}

	public void delete(String email) {
		hashRedisRepository.delete(email);
	}
}
