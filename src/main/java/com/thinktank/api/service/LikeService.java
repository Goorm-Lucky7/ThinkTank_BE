package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.like.request.LikeCreateDto;
import com.thinktank.api.entity.Like;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.UserLikeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final UserLikeRepository userLikeRepository;
	private final UserRepository userRepository;

	@Transactional
	public void handleLike(LikeCreateDto likeCreateDto, Long userId) {
		Long postId = likeCreateDto.postId();
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));

		Like like = likeRepository.findByPost(post).orElse(null);

		if (like == null) {
			createLike(likeCreateDto, userId);
		} else {
			processLike(like, userId);
		}
	}

	private void processLike(Like like, Long userId) {
		boolean isUserLiked = userLikeRepository.existsByLikeIdAndUserId(like.getId(), userId);

		if (isUserLiked) {
			UserLike userLike = userLikeRepository.findByLikeIdAndUserId(like.getId(), userId)
				.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_LIKE_FOUND_EXCEPTION));
			if (userLike.isCheck()) {
				cancelLike(like, userId);
			} else {
				userLike.recreateLike();
				userLikeRepository.save(userLike);
				like.incrementLikeCount();
				likeRepository.save(like);
			}
		} else {
			like.incrementLikeCount();
			likeRepository.save(like);
			saveUserLike(like, userId);
		}
	}

	private void createLike(LikeCreateDto likeCreateDto, Long userId) {
		Post post = postRepository.findById(likeCreateDto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));
		Like newLike = Like.builder()
			.post(post)
			.build();
		Like savedLike = likeRepository.save(newLike);
		saveUserLike(savedLike, userId);
	}

	private void cancelLike(Like like, Long userId) {
		like.decrementLikeCount();
		likeRepository.save(like);
		UserLike userLike = userLikeRepository.findByLikeIdAndUserId(like.getId(), userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_LIKE_FOUND_EXCEPTION));
		userLike.cancelLike();
		userLikeRepository.save(userLike);
	}

	private void saveUserLike(Like like, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		UserLike userLike = UserLike.builder()
			.user(user)
			.like(like)
			.isCheck(true)
			.build();
		userLikeRepository.save(userLike);
	}
}