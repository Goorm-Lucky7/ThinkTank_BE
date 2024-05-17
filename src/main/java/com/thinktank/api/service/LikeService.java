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
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.exception.UnauthorizedException;
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
		final User user = findUserByEmail(authUser.email());
		final Post post = findByPostId(likeCreateDto.postId());

		likeRepository.findByPost(post)
			.ifPresentOrElse(
				existingLike -> processLike(existingLike, user),
				() -> createLike(likeCreateDto, user)
			);
	}

	private void processLike(Like like, User user) {
		boolean isUserLiked = userLikeRepository.existsByLikeIdAndUserId(like.getId(), user.getId());

		if (isUserLiked) {
			final UserLike userLike = findByLikeIdAndUserId(like.getId(), user.getId());
			if (userLike.isCheck()) {
				cancelLike(like, user);
			} else {
				userLike.activateLike();
				like.incrementLikeCount();
			}
		} else {
			like.incrementLikeCount();
			saveUserLike(like, user);
		}
	}

	private void createLike(LikeCreateDto likeCreateDto, User user) {
		final Post post = findByPostId(likeCreateDto.postId());
		Like newLike = Like.builder()
			.post(post)
			.build();
		Like savedLike = likeRepository.save(newLike);
		saveUserLike(savedLike, user);
	}

	private void cancelLike(Like like, User user) {
		like.decrementLikeCount();
		final UserLike userLike = findByLikeIdAndUserId(like.getId(), user.getId());
		userLike.deactivateLike();
	}

	private void saveUserLike(Like like, User user) {
		final User userStatus = checkUserLikeState(user.getId());
		UserLike userLike = UserLike.builder()
			.user(userStatus)
			.like(like)
			.isCheck(true)
			.build();
		userLikeRepository.save(userLike);
	}
	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
	}

	private Post findByPostId(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_POST_NOT_FOUND));
	}

	private UserLike findByLikeIdAndUserId(Long likeId, Long userId) {
		return userLikeRepository.findByLikeIdAndUserId(likeId, userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_USER_LIKE_NOT_FOUND));
	}

	private User checkUserLikeState(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_USER_NOT_FOUND));
	}
}
