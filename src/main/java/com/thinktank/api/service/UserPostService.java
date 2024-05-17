package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

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
import com.thinktank.global.error.exception.UnauthorizedException;
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
		String userEmail = Optional.ofNullable(authUser)
			.map(AuthUser::email)
			.orElse(null);
		final User user = findUserByEmail(email);

		Pageable pageable = PageRequest.of(page, size);
		Page<? extends PostResponseDto> postsPage = processProblemType(value, user.getEmail(), userEmail, pageable);
		List<? extends PostResponseDto> posts = postsPage.getContent();
		UserProfileResDto userRes = toUser(user.getEmail());

		PageInfo pageInfo = new PageInfo(
			postsPage.getNumber(),
			postsPage.isLast()
		);
		return new PagePostProfileResponseDto(userRes, posts, pageInfo);
	}

	public Page<? extends PostResponseDto> processProblemType(String value, String email, String me,
		Pageable pageable) {
		ProblemType problemType = ProblemType.fromValue(value);
		if (problemType != null) {
			return switch (problemType) {
				case CREATED -> processCreatedProblems(email, me, pageable);
				case SOLVED -> processSolvedProblems(email, pageable);
				case LIKED -> processLikedProblems(email, me, pageable);
			};
		} else {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_REQUEST);
		}
	}

	private Page<? extends PostResponseDto> processCreatedProblems(String email, String me, Pageable pageable) {
		final User user = findUserByEmail(email);
		Page<Post> createdPostsPage = postRepository.findByUser(user, pageable);
		return createdPostsPage.map(post -> toPostNotSolved(post, me));
	}

	private Page<? extends PostResponseDto> processSolvedProblems(String email, Pageable pageable) {
		final User user = findUserByEmail(email);
		Page<UserCode> userCodesPage = userCodeRepository.findByUser(user, pageable);

		return userCodesPage.map(userCode -> {
			Post post = userCode.getPost();
			return toPostSolved(post);
		});
	}

	private Page<? extends PostResponseDto> processLikedProblems(String email, String me, Pageable pageable) {
		final User user = findUserByEmail(email);
		Page<UserLike> userLikesPage = userLikeRepository.findByUserAndIsCheckTrue(user, pageable);

		return userLikesPage.map(userLike -> {
			Post post = userLike.getLike().getPost();
			return toPostNotSolved(post, me);
		});
	}

	private UserProfileResDto toUser(String email) {
		User user = findUserByEmail(email);
		String profileImage = profileImageRepository.findByUserId(user.getId());

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
			post.getId(), post.getId() + THOUSAND, post.getTitle(), post.getCategory().toString(),
			post.getCreatedAt(), post.getContent(), likeType, commentCount, likeCount, codeCount
		);
	}

	private PostSolvedResponseDto toPostSolved(Post post) {
		UserCode userCode = userCodeRepository.findByPostId(post.getId());
		return new PostSolvedResponseDto(
			post.getId(), post.getId() + THOUSAND, post.getLanguage().toString(), post.getTitle(),
			userCode.getCode()
		);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
	}

	private boolean isPostLikedByUser(String email, Post post) {
		return Optional.ofNullable(email)
			.map(e -> userLikeService.isPostLikedByUser(e, post.getId()))
			.orElse(false);
	}
}
