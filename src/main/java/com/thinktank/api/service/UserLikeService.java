package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.entity.UserLike;
import com.thinktank.api.repository.UserLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLikeService {
	private final UserLikeRepository userLikeRepository;

	public boolean isPostLikedByUser(Long userId, Long postId) {
		UserLike userLike = userLikeRepository.findByUserIdAndLikePostId(userId, postId);
		return userLike != null && userLike.isCheck();
	}
}
