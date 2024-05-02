package com.thinktank.api.service;

import static com.thinktank.global.common.util.AuthConstants.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.dto.comment.request.CommentDeleteDto;
import com.thinktank.api.dto.comment.response.CommentPageResponseDto;
import com.thinktank.api.dto.comment.response.CommentResponseDto;
import com.thinktank.api.dto.comment.response.CommentUserResponseDto;
import com.thinktank.api.dto.comment.response.CommentsResponseDto;
import com.thinktank.api.entity.Comment;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.api.service.auth.JwtProviderService;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private static final int DEFAULT_PAGE_INDEX = 0;
	private static final int DEFAULT_PAGE_SIZE = 10;

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final JwtProviderService jwtProviderService;

	@Transactional
	public void createComment(CommentCreateDto commentCreateDto, HttpServletRequest request) {
		String accessToken = jwtProviderService.extractAccessToken(ACCESS_TOKEN_HEADER, request);
		AuthUser authUser = jwtProviderService.extractAuthUserByAccessToken(accessToken);

		final User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));

		final Post post = postRepository.findById(commentCreateDto.postId())
				.orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST));

		Comment comment = Comment.create(commentCreateDto, user, post);
		commentRepository.save(comment);
	}

	@Transactional(readOnly = true)
	public CommentsResponseDto getCommentsByPostId(Long postId, Integer pageIndex, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageIndex == null ? DEFAULT_PAGE_INDEX : pageIndex,
											pageSize == null ? DEFAULT_PAGE_SIZE : pageSize);
		Page<Comment> page = commentRepository.findByPostId(postId, pageable);

		List<CommentResponseDto> comments = page.getContent().stream()
			.map(comment -> new CommentResponseDto(
				comment.getId(),
				comment.getContent(),
				comment.getCreatedAt().toString(),
				new CommentUserResponseDto(comment.getUser().getNickname())
			))
			.collect(Collectors.toList());

		CommentPageResponseDto pageRequestDto = new CommentPageResponseDto(pageIndex, !page.hasNext());

		return new CommentsResponseDto(postId, comments, pageRequestDto);
	}

	@Transactional
	public void deleteComment(CommentDeleteDto commentDeleteDto, HttpServletRequest request) {
		String accessToken = jwtProviderService.extractAccessToken(ACCESS_TOKEN_HEADER, request);
		AuthUser authUser = jwtProviderService.extractAuthUserByAccessToken(accessToken);

		Comment comment = commentRepository.findById(commentDeleteDto.commentId())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_COMMENT_FOUND_EXCEPTION));

		boolean isUserComment = validateUserComment(comment, authUser.email());
		boolean isCommentInUserPost = validateCommentInUserPost(commentDeleteDto.postId(), authUser.email());

		if(!isUserComment && !isCommentInUserPost) {
			throw new BadRequestException(ErrorCode.DELETE_COMMENT_FORBIDDEN_EXCEPTION);
		}

		commentRepository.delete(comment);
	}

	private boolean validateUserComment(Comment comment, String userEmail) {
		return comment.getUser().getEmail().equals(userEmail);
	}

	private boolean validateCommentInUserPost(Long postId, String userEmail) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_NOT_POST_FOUND_EXCEPTION));

		return post.getUser().getEmail.equals(userEmail);
	}
}
