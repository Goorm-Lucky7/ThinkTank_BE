package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.page.response.PageInfo;
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
import com.thinktank.api.entity.ProfileImage;
import com.thinktank.api.entity.TestCase;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.UserLike;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.ProfileImageRepository;
import com.thinktank.api.repository.TestCaseRepository;
import com.thinktank.api.repository.UserCodeRepository;
import com.thinktank.api.repository.UserLikeRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.common.util.JavaJudge;
import com.thinktank.global.common.util.JavaScriptJudge;
import com.thinktank.global.common.util.JudgeUtil;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.exception.UnauthorizedException;
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
	private final ProfileImageRepository profileImageRepository;
	private final UserLikeService userLikeService;

	public void createPost(PostCreateDto postCreateDto, AuthUser authUser) {
		final User user = findUserByEmail(authUser.email());

		validateCategory(postCreateDto.category());
		validateLanguage(postCreateDto.language());

		final Post post = Post.create(postCreateDto, user);
		final List<CustomTestCase> customTestCases = postCreateDto.testCases();

		validateJudge(customTestCases, postCreateDto.code(), postCreateDto.language());

		final List<TestCase> testCases = customTestCases.stream()
			.map(customTestCase -> TestCase.createTestCase(customTestCase, post))
			.toList();

		postRepository.save(post);
		testCaseRepository.saveAll(testCases);
	}

	@Transactional(readOnly = true)
	public PagePostResponseDto getAllPosts(int page, int size, AuthUser authUser) {
		Page<Post> postPage = postRepository.findAll(PageRequest.of(page, size));

		List<PostsResponseDto> postsResponseDtoList = convertPostListToDtoList(postPage.getContent(), authUser);

		PageInfo pageInfo = new PageInfo(postPage.getNumber(), postPage.isLast());

		return new PagePostResponseDto(postsResponseDtoList, pageInfo);
	}

	@Transactional(readOnly = true)
	public PostDetailResponseDto getPostDetail(Long postId, AuthUser authUser) {
		String userEmail = Optional.ofNullable(authUser)
			.map(AuthUser::email)
			.orElse(null);

		final Post post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_POST_NOT_FOUND));

		List<CustomTestCase> testCases = testCaseRepository.findByPostId(postId);

		return convertPostToDetailResponseDto(post, testCases, userEmail);
	}

	public void deletePost(PostDeleteDto postDeleteDto, AuthUser authUser) {
		final User user = findUserByEmail(authUser.email());
		final Post post = findPostById(postDeleteDto.postId());

		validateUserOwnership(user, post);

		deletePostData(postDeleteDto.postId());
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
	}

	private Post findPostById(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_POST_NOT_FOUND));
	}

	private List<PostsResponseDto> convertPostListToDtoList(List<Post> posts, AuthUser authUser) {
		if (authUser == null) {
			return posts.stream()
				.map(post -> convertPostToResponseDto(post, null))
				.toList();
		}

		return posts.stream()
			.map(post -> convertPostToResponseDto(post, authUser.email()))
			.toList();
	}

	private PostsResponseDto convertPostToResponseDto(Post post, String userEmail) {
		final int commentCount = commentRepository.countCommentsByPost(post);
		final int likeCount = likeRepository.findLikeCountByPost(post);
		final int codeCount = userCodeRepository.countUserCodeByPost(post);
		final boolean likeType = userLikeService.isPostLikedByUser(userEmail, post.getId());

		SimpleUserResDto simpleUserResDto = createSimpleUserResDto(post);

		return createPostsResponseDto(post, commentCount, likeCount, codeCount, likeType, simpleUserResDto);
	}

	private PostsResponseDto createPostsResponseDto(Post post, int commentCount, int likeCount, int codeCount,
		boolean likeType, SimpleUserResDto simpleUserResDto) {

		return new PostsResponseDto(
			post.getId(), post.getId() + THOUSAND, post.getTitle(), post.getCategory().toString(),
			post.getCreatedAt(), post.getContent(), commentCount, likeCount, codeCount, likeType, simpleUserResDto);
	}

	private PostDetailResponseDto convertPostToDetailResponseDto(Post post, List<CustomTestCase> testCases,
		String userEmail) {
		final int commentCount = commentRepository.countCommentsByPost(post);
		final int likeCount = likeRepository.findLikeCountByPost(post);
		final int codeCount = userCodeRepository.countUserCodeByPost(post);
		final boolean likeType = userLikeService.isPostLikedByUser(userEmail, post.getId());
		final boolean isOwner = post.getUser().getEmail().equals(userEmail);

		return createPostDetailResponseDto(post, testCases, commentCount, likeCount, codeCount, likeType, isOwner);
	}

	private PostDetailResponseDto createPostDetailResponseDto(Post post, List<CustomTestCase> testCases,
		int commentCount, int likeCount, int codeCount, boolean likeType, boolean isOwner) {
		return new PostDetailResponseDto(post.getId(),
			post.getId() + THOUSAND, post.getTitle(), post.getCategory().toString(), post.getCreatedAt(),
			post.getContent(), testCases, post.getCondition(), isOwner, likeCount, commentCount, codeCount,
			post.getLanguage().toString(), likeType, post.getCode()
		);

	}

	private SimpleUserResDto createSimpleUserResDto(Post post) {
		final User user = findUserByEmail(post.getUser().getEmail());

		final ProfileImage profileImage = profileImageRepository.findByUserEmail(user.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_IMAGE_NOT_FOUND));

		return new SimpleUserResDto(user.getEmail(), user.getNickname(), profileImage.getProfileImage());
	}
	private void deletePostData(Long postId) {
		userCodeRepository.deleteByPostId(postId);
		commentRepository.deleteByPostId(postId);
		testCaseRepository.deleteByPostId(postId);
		List<UserLike> userLikes = userLikeRepository.findByLikePostId(postId);
		userLikeRepository.deleteAll(userLikes);
		likeRepository.deleteByPostId(postId);
		postRepository.deleteById(postId);
	}

	private void validateCategory(String category) {
		if (!Category.isValidCategory(category)) {
			throw new BadRequestException(ErrorCode.FAIL_CATEGORY_NOT_FOUND);
		}
	}

	private void validateLanguage(String language) {
		if (!Language.isValidLanguage(language)) {
			throw new BadRequestException(ErrorCode.FAIL_UNSUPPORTED_LANGUAGE);
		}
	}

	private void validateUserOwnership(User user, Post post) {
		if (!Objects.equals(user.getId(), post.getUser().getId())) {
			throw new BadRequestException(ErrorCode.FAIL_POST_DELETION_FORBIDDEN);
		}
	}

	private void validateJudge(List<CustomTestCase> testCases, String code, String language) {
		final JudgeUtil judgeService;

		if (language.equals("java")) {
			judgeService = new JavaJudge();
		} else if (language.equals("javascript")) {
			judgeService = new JavaScriptJudge();
		} else {
			throw new NotFoundException(ErrorCode.FAIL_POST_NOT_FOUND);
		}

		judgeService.executeCode(testCases, code);
	}
}
