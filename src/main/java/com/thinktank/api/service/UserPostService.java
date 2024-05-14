package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.page.response.PageInfo;
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
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.ProfileImageRepository;
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
	private final ProfileImageRepository profileImageRepository;
	private final UserLikeService userLikeService;

	public PagePostProfileResponseDto getProfilePosts(int page, int size, String value, String email,
		AuthUser authUser) {
		Optional<AuthUser> optionalAuthUser = Optional.ofNullable(authUser);
		String authUserEmail = optionalAuthUser.map(AuthUser::email).orElse(null);

		Long userId = userRepository.findUserIdByEmail(email);
		if (userId == null) {
			throw new BadRequestException(ErrorCode.FAIL_USER_NOT_FOUND);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Post> postsWithProfile = postRepository.findAll(pageable);

		List<? extends PostResponseDto> posts = processProblemType(value, userId, authUserEmail);
		UserProfileResDto userRes = toUser(userId);
		PageInfo pageInfo = new PageInfo(
			postsWithProfile.getNumber(),
			postsWithProfile.isLast()
		);
		return new PagePostProfileResponseDto(userRes, posts, pageInfo);
	}

	public List<? extends PostResponseDto> processProblemType(String value, Long userId, String me) {
		ProblemType problemType = ProblemType.fromValue(value);
		if (problemType != null) {
			return switch (problemType) {
				case CREATED -> processCreatedProblems(userId, me);
				case SOLVED -> processSolvedProblems(userId);
				case LIKED -> processLikedProblems(userId, me);
			};
		} else {
			throw new BadRequestException(ErrorCode.FAIL_LOGIN_REQUIRED);
		}
	}

	private List<? extends PostResponseDto> processCreatedProblems(Long userId, String me) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_USER_NOT_FOUND));
		List<Post> createdPosts = postRepository.findByUser(user);
		List<PostProfileResponseDto> createdPostDtos = new ArrayList<>();
		for (Post post : createdPosts) {
			PostProfileResponseDto postDto = toPostNotSolved(post, me);
			createdPostDtos.add(postDto);
		}
		return createdPostDtos;
	}

	private List<? extends PostResponseDto> processSolvedProblems(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_USER_NOT_FOUND));
		List<UserCode> userCodes = userCodeRepository.findByUser(user);
		List<PostSolvedResponseDto> solvedPostDtos = new ArrayList<>();
		for (UserCode userCode : userCodes) {
			Post post = userCode.getPost();
			PostSolvedResponseDto postDto = toPostSolved(post);
			solvedPostDtos.add(postDto);
		}
		return solvedPostDtos;
	}

	private List<? extends PostResponseDto> processLikedProblems(Long userId, String me) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_USER_NOT_FOUND));
		List<UserLike> userLikes = userLikeRepository.findByUserAndIsCheckTrue(user);
		List<PostProfileResponseDto> likedPostDtos = new ArrayList<>();
		for (UserLike userLike : userLikes) {
			Post post = userLike.getLike().getPost();
			PostProfileResponseDto postDto = toPostNotSolved(post, me);
			likedPostDtos.add(postDto);
		}
		return likedPostDtos;
	}

	private UserProfileResDto toUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_USER_NOT_FOUND));
		String profileImage = profileImageRepository.findByUserId(userId);

		return new UserProfileResDto(user.getEmail(), user.getNickname(), user.getGithub(), user.getBlog(),
			user.getIntroduce(), profileImage
		);
	}

	private PostProfileResponseDto toPostNotSolved(Post post, String me) {
		int commentCount = commentRepository.countCommentsByPost(post);
		int likeCount = likeRepository.findLikeCountByPost(post);
		int codeCount = userCodeRepository.countUserCodeByPost(post);
		boolean likeType = isPostLikedByUser(me, post);

		return new PostProfileResponseDto(
			post.getId(), post.getId() + THOUSAND, post.getTitle(), post.getCategory().toString(), likeType,
			commentCount, likeCount, codeCount
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

	private boolean isPostLikedByUser(String email, Post post) {
		if (email == null) {
			return false;
		}
		return userLikeService.isPostLikedByUser(email, post.getId());
	}
}
