package com.thinktank.api.service;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.page.response.PageInfoDto;
import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.dto.post.response.PagePostResponseDto;
import com.thinktank.api.dto.post.response.PostsResponseDto;
import com.thinktank.api.dto.user.response.SimpleUserResDto;
import com.thinktank.api.entity.Category;
import com.thinktank.api.entity.Language;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.LikeRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final UserLikeService userLikeService;

	public void createPost(PostCreateDto postCreateDto) {
		validateCategory(postCreateDto.category());
		validateLanguage(postCreateDto.language());
		Post post = Post.create(postCreateDto);
		postRepository.save(post);
	}

	public PagePostResponseDto getAllPosts(int page, int size, Long userId) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> postPage = postRepository.findAll(pageable);

		String profileImage = null;

		List<PostsResponseDto> posts = postPage.getContent().stream()
			.map(post -> toPost(post, profileImage, userId))
			.collect(Collectors.toList());

		PageInfoDto pageInfoDto = new PageInfoDto(
			postPage.getNumber(),
			postPage.isLast()
		);

		return new PagePostResponseDto(posts, pageInfoDto);
	}

	private PostsResponseDto toPost(Post post, String profileImage, Long userId) {
		SimpleUserResDto userDto = toUser(post.getUser(), profileImage);
		int commentCount = commentRepository.countCommentsByPost(post);
		int likeCount = likeRepository.findLikeCountByPost(post);
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
			post.getAnswerCount(),
			likeType,
			userDto
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
