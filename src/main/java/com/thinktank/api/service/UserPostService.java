package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.page.response.PageInfoDto;
import com.thinktank.api.dto.post.response.PagePostProfileResponseDto;
import com.thinktank.api.dto.post.response.PostProfileResponseDto;
import com.thinktank.api.dto.post.response.PostResponseDto;
import com.thinktank.api.dto.post.response.PostSolvedResponseDto;
import com.thinktank.api.dto.user.response.UserProfileResDto;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.ProblemType;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserCode;
import com.thinktank.api.entity.UserLike;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.UserCodeRepository;
import com.thinktank.api.repository.UserLikeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final UserLikeRepository userLikeRepository;
	private final CommentRepository commentRepository;
	private final UserCodeRepository userCodeRepository;
	private final UserLikeService userLikeService;

	public PagePostProfileResponseDto getProfilePosts(int page, int size, String value, Long userId, Long loginUserId) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> postsWithProfile = postRepository.findAll(pageable);
		String profileImage = null;
		List<? extends PostResponseDto> posts = processProblemType(value, userId, loginUserId);
		UserProfileResDto user = toUser(userId, profileImage);
		PageInfoDto pageInfo = new PageInfoDto(
			postsWithProfile.getNumber(),
			postsWithProfile.isLast()
		);
		return new PagePostProfileResponseDto(user, posts, pageInfo);
	}

	public List<? extends PostResponseDto> processProblemType(String value, Long userId, Long loginUserId) {
		ProblemType problemType = ProblemType.fromValue(value);
		if (problemType != null) {
			return switch (problemType) {
				case CREATED -> processCreatedProblems(userId, loginUserId);
				case SOLVED -> processSolvedProblems(userId);
				case LIKED -> processLikedProblems(userId, loginUserId);
			};
		} else {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private List<? extends PostResponseDto> processCreatedProblems(Long userId, Long loginUserId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		List<Post> createdPosts = postRepository.findByUser(user);
		List<PostProfileResponseDto> createdPostDtos = new ArrayList<>();
		for (Post post : createdPosts) {
			PostProfileResponseDto postDto = toPostNotSolved(post, loginUserId);
			createdPostDtos.add(postDto);
		}
		return createdPostDtos;
	}

	private List<? extends PostResponseDto> processSolvedProblems(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		List<UserCode> userCodes = userCodeRepository.findByUser(user);
		List<PostSolvedResponseDto> solvedPostDtos = new ArrayList<>();
		for (UserCode userCode : userCodes) {
			Post post = userCode.getPost();
			PostSolvedResponseDto postDto = toPostSolved(post);
			solvedPostDtos.add(postDto);
		}
		return solvedPostDtos;
	}

	private List<? extends PostResponseDto> processLikedProblems(Long userId, Long loginUserId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		List<UserLike> userLikes = userLikeRepository.findByUserAndIsCheckTrue(user);
		List<PostProfileResponseDto> likedPostDtos = new ArrayList<>();
		for (UserLike userLike : userLikes) {
			Post post = userLike.getLike().getPost();
			PostProfileResponseDto postDto = toPostNotSolved(post, loginUserId);
			likedPostDtos.add(postDto);
		}
		return likedPostDtos;
	}

	private UserProfileResDto toUser(Long userId, String profileImage) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_USER_FOUND_EXCEPTION));
		return new UserProfileResDto(
			user.getEmail(),
			user.getNickname(),
			profileImage,
			user.getIntroduce()
		);
	}

	private PostProfileResponseDto toPostNotSolved(Post post, Long loginUserId) {
		int commentCount = commentRepository.countCommentsByPost(post);
		int likeCount = likeRepository.findLikeCountByPost(post);
		int answerCount = userCodeRepository.countUserCodeByPost(post);
		boolean likeType = isPostLikedByUser(loginUserId, post);

		return new PostProfileResponseDto(
			post.getId(),
			post.getId() + THOUSAND,
			post.getTitle(),
			post.getCategory().toString(),
			likeType,
			commentCount,
			likeCount,
			answerCount
		);
	}

	private PostSolvedResponseDto toPostSolved(Post post) {
		UserCode userCode = userCodeRepository.findByPostId(post.getId());
		return new PostSolvedResponseDto(
			post.getId(),
			post.getId() + THOUSAND,
			post.getLanguage().toString(),
			post.getTitle(),
			userCode.getCode()
		);
	}

	private boolean isPostLikedByUser(Long loginUserId, Post post) {
		if (loginUserId == null) {
			return false;
		}
		return userLikeService.isPostLikedByUser(loginUserId, post.getId());
	}
}
