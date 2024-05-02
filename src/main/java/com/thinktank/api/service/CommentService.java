package com.thinktank.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.dto.comment.request.CommentPageRequestDto;
import com.thinktank.api.dto.comment.response.CommentResponseDto;
import com.thinktank.api.dto.comment.response.CommentUserResponseDto;
import com.thinktank.api.dto.comment.response.CommentsResponse;
import com.thinktank.api.entity.Comment;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private static final int CONTENT_MAX_LENGTH = 100;

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createComment(CommentCreateDto commentCreateDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		AuthUser authUser = (AuthUser) authentication.getPrincipal();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION);
		}

		User user = userRepository.findByEmail(authUser.email())
			.orElseThrow(() -> new BadRequestException(ErrorCode.FAIL_UNAUTHORIZED_EXCEPTION));

		final Post post = postRepository.findById(commentCreateDto.postId())
				.orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST));

		Comment comment = Comment.create(commentCreateDto, post);
		commentRepository.save(comment);
	}

	@Transactional(readOnly = true)
	public CommentsResponse getCommentsByPostId(Long postId, int pageIndex, int pageSize) {
		Page<Comment> page = commentRepository.findByPostId(postId, PageRequest.of(pageIndex, pageSize));
		List<CommentResponseDto> comments = page.getContent().stream()
			.map(comment -> new CommentResponseDto(
				comment.getId(),
				comment.getContent(),
				comment.getCreatedAt().toString(),
				new CommentUserResponseDto(comment.getUser().getNickname())
			))
			.collect(Collectors.toList());

		CommentPageRequestDto pageRequestDto = new CommentPageRequestDto(pageIndex, !page.hasNext());

		return new CommentsResponse(postId, comments, pageRequestDto);
	}
}
