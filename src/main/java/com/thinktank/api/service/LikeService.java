package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.like.request.LikeCreateDto;
import com.thinktank.api.entity.Like;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.UserLikeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final UserLikeRepository userLikeRepository;
	private final UserRepository userRepository;

	public void handleLike(LikeCreateDto likeCreateDto, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));
		Post post = postRepository.findById(likeCreateDto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));

		Like like = likeRepository.findByPost(post).orElse(null);

		if (like == null) {
			createLike(likeCreateDto, user);
		} else {
			processLike(like, user);
		}
	}

	private void processLike(Like like, User user) {
		boolean isUserLiked = userLikeRepository.existsByLikeIdAndUserId(like.getId(), user.getId());

		if (isUserLiked) {
			UserLike userLike = userLikeRepository.findByLikeIdAndUserId(like.getId(), user.getId())
				.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_LIKE_FOUND_EXCEPTION));
			if (userLike.isCheck()) {
				cancelLike(like, user);
			} else {
				userLike.activateLike();
				userLikeRepository.save(userLike);
				like.incrementLikeCount();
				likeRepository.save(like);
			}
		} else {
			like.incrementLikeCount();
			likeRepository.save(like);
			saveUserLike(like, user);
		}
	}

	private void createLike(LikeCreateDto likeCreateDto, User user) {
		Post post = postRepository.findById(likeCreateDto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));
		Like newLike = Like.builder()
			.post(post)
			.build();
		Like savedLike = likeRepository.save(newLike);
		saveUserLike(savedLike, user);
	}

	private void cancelLike(Like like, User user) {
		like.decrementLikeCount();
		likeRepository.save(like);
		UserLike userLike = userLikeRepository.findByLikeIdAndUserId(like.getId(), user.getId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_LIKE_FOUND_EXCEPTION));
		userLike.deactivateLike();
		userLikeRepository.save(userLike);
	}

	private void saveUserLike(Like like, User user) {
		User userStatus = userRepository.findById(user.getId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		UserLike userLike = UserLike.builder()
			.user(userStatus)
			.like(like)
			.isCheck(true)
			.build();
		userLikeRepository.save(userLike);
	}
}