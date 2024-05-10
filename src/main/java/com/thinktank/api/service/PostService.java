package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.page.response.PageInfoDto;
import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.dto.post.request.PostDeleteDto;
import com.thinktank.api.dto.post.response.PagePostResponseDto;
import com.thinktank.api.dto.post.response.PostDetailResponseDto;
import com.thinktank.api.dto.post.response.PostsResponseDto;
import com.thinktank.api.dto.testcase.custom.CustomTestCase;
import com.thinktank.api.dto.user.response.SimpleUserResDto;
import com.thinktank.api.entity.Category;
import com.thinktank.api.entity.Language;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.TestCase;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.TestCaseRepository;
import com.thinktank.api.repository.UserCodeRepository;
import com.thinktank.api.repository.UserLikeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.common.util.JavaJudge;
import com.thinktank.global.common.util.JavaScriptJudge;
import com.thinktank.global.common.util.JudgeUtil;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final TestCaseRepository testCaseRepository;
	private final UserLikeRepository userLikeRepository;
	private final UserCodeRepository userCodeRepository;
	private final UserLikeService userLikeService;

	public void createPost(PostCreateDto postCreateDto, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));

		validateCategory(postCreateDto.category());
		validateLanguage(postCreateDto.language());

		final Post post = Post.create(postCreateDto, user);
		final List<CustomTestCase> customTestCases = postCreateDto.testCases();

		validateJudge(customTestCases, postCreateDto.answer(), postCreateDto.language());

		final List<TestCase> testCases = customTestCases.stream()
			.map(customTestCase -> TestCase.createTestCase(customTestCase, post))
			.toList();

		postRepository.save(post);
		testCaseRepository.saveAll(testCases);
	}

	private void validateJudge(List<CustomTestCase> testCases, String code, String language) {
		final JudgeUtil judgeService;

		if (language.equals("java")) {
			judgeService = new JavaJudge();
		} else if (language.equals("javascript")) {
			judgeService = new JavaScriptJudge();
		} else {
			throw new BadRequestException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION);
		}

		judgeService.executeCode(testCases, code);
	}

	@Transactional(readOnly = true)
	public PagePostResponseDto getAllPosts(int page, int size, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> postPage = postRepository.findAll(pageable);

		String profileImage = null;

		List<PostsResponseDto> posts = postPage.getContent().stream()
			.map(post -> toPost(post, profileImage, user.getId()))
			.toList();

		PageInfoDto pageInfo = new PageInfoDto(
			postPage.getNumber(),
			postPage.isLast()
		);

		return new PagePostResponseDto(posts, pageInfo);
	}

	@Transactional(readOnly = true)
	public PostDetailResponseDto getPostDetail(Long postId, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));
		List<CustomTestCase> testCases = testCaseRepository.findByPostId(postId);

		return mapToPostDetailResponseDto(post, testCases, user.getId());
	}

	public void deletePost(PostDeleteDto postDeleteDto, AuthUser authUser) {
		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));
		Post post = postRepository.findById(postDeleteDto.postId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));

		if (!Objects.equals(user.getId(), post.getUser().getId())) {
			throw new BadRequestException(ErrorCode.DELETE_POST_FORBIDDEN_EXCEPTION);
		}
		userCodeRepository.deleteByPostId(postDeleteDto.postId());
		commentRepository.deleteByPostId(postDeleteDto.postId());
		testCaseRepository.deleteByPostId(postDeleteDto.postId());
		List<UserLike> userLikes = userLikeRepository.findByLikePostId(postDeleteDto.postId());
		userLikeRepository.deleteAll(userLikes);
		likeRepository.deleteByPostId(postDeleteDto.postId());
		postRepository.delete(post);
	}

	private PostDetailResponseDto mapToPostDetailResponseDto(Post post, List<CustomTestCase> testCases, Long userId) {
		int commentCount = commentRepository.countCommentsByPost(post);
		int likeCount = likeRepository.findLikeCountByPost(post);
		int answerCount = userCodeRepository.countUserCodeByPost(post);
		boolean likeType = isPostLikedByUser(userId, post);
		return new PostDetailResponseDto(
			post.getId(),
			post.getTitle(),
			post.getCategory().toString(),
			post.getCreatedAt(),
			post.getContent(),
			testCases,
			post.getCondition(),
			post.getUser().getId().equals(userId),
			likeCount,
			commentCount,
			answerCount,
			post.getLanguage().toString(),
			likeType,
			post.getAnswer()
		);
	}

	private PostsResponseDto toPost(Post post, String profileImage, Long userId) {
		SimpleUserResDto user = toUser(post.getUser(), profileImage);
		int commentCount = commentRepository.countCommentsByPost(post);
		int likeCount = likeRepository.findLikeCountByPost(post);
		int answerCount = userCodeRepository.countUserCodeByPost(post);
		boolean likeType = isPostLikedByUser(userId, post);

		return new PostsResponseDto(
			post.getId(),
			post.getId() + THOUSAND,
			post.getTitle(),
			post.getCategory().toString(),
			post.getCreatedAt(),
			post.getContent(),
			commentCount,
			likeCount,
			answerCount,
			likeType,
			user
		);
	}

	private SimpleUserResDto toUser(User user, String profileImage) {
		return new SimpleUserResDto(
			user.getNickname(),
			profileImage
		);
	}

	private boolean isPostLikedByUser(Long userId, Post post) {
		return userLikeService.isPostLikedByUser(userId, post.getId());
	}

	private void validateCategory(String category) {
		if (!Category.isValidCategory(category)) {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_CATEGORY);
		}
	}

	private void validateLanguage(String language) {
		if (!Language.isValidLanguage(language)) {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_LANGUAGE);
		}
	}
}
