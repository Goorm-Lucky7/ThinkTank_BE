package com.thinktank.api.repository.redis;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinktank.api.dto.auth.TokenSaveValue;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

@Repository
public class HashRedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private final HashOperations<String, String, Object> hashOperations;
	private final Jackson2HashMapper jackson2HashMapper;
	private final ObjectMapper objectMapper;

	public HashRedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		hashOperations = redisTemplate.opsForHash();
		jackson2HashMapper = new Jackson2HashMapper(false);
		this.objectMapper = objectMapper;
	}

	public void save(String key, Object value, Duration timeout) {
		hashOperations.putAll(key, jackson2HashMapper.toHash(value));
		redisTemplate.expire(key, timeout);
	}

	public Object get(String key) {
		Map<String, Object> userToken = hashOperations.entries(key);

		if (userToken.isEmpty()) {
			throw new UnauthorizedException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION);
		}

		return objectMapper.convertValue(userToken, TokenSaveValue.class);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
