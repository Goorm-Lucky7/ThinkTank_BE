package com.thinktank.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.dto.comment.request.CommentDeleteDto;
import com.thinktank.api.dto.comment.response.CommentResDto;
import com.thinktank.api.dto.comment.response.CommentUserResDto;
import com.thinktank.api.dto.comment.response.CommentsResDto;
import com.thinktank.api.dto.page.response.PageInfo;
import com.thinktank.api.entity.Comment;
import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.User;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.api.repository.ProfileImageRepository;
import com.thinktank.api.repository.UserRepository;
import com.thinktank.global.error.exception.NotFoundException;
import com.thinktank.global.error.exception.UnauthorizedException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ProfileImageRepository profileImageRepository;

	@Transactional
	public void createComment(CommentCreateDto commentCreateDto, AuthUser authUser) {
		final User user = findUserByEmail(authUser.email());
		final Post post = findPostById(commentCreateDto.postId());

		createComment(commentCreateDto, user, post);
	}

	@Transactional(readOnly = true)
	public CommentsResDto getCommentsByPostId(Long postId, AuthUser authUser, int pageIndex, int pageSize) {
		Pageable pageable = createPageable(pageIndex, pageSize);
		Page<Comment> page = findCommentsByPostId(postId, pageable);
		List<CommentResDto> comments = convertToCommentResDtoList(page, authUser);

		PageInfo pageInfo = createPageInfo(page, pageIndex);

		return new CommentsResDto(postId, comments, pageInfo);
	}

	@Transactional
	public void deleteComment(CommentDeleteDto commentDeleteDto, AuthUser authUser) {
		Comment comment = findCommentById(commentDeleteDto.commentId());

		validateDeletionRights(comment, commentDeleteDto.postId(), authUser);

		commentRepository.delete(comment);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
	}

	private Post findPostById(Long id) {
		return postRepository.findById(id)
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.FAIL_LOGIN_REQUIRED));
	}

	private void createComment(CommentCreateDto commentCreateDto, User user, Post post) {
		commentRepository.save(Comment.create(commentCreateDto, user, post));
	}

	private Pageable createPageable(int pageIndex, int pageSize) {
		return PageRequest.of(pageIndex, pageSize);
	}

	private Page<Comment> findCommentsByPostId(Long id, Pageable pageable) {
		return commentRepository.findByPostId(id, pageable);
	}

	private List<CommentResDto> convertToCommentResDtoList(Page<Comment> page, AuthUser authUser) {
		if (authUser == null) {
			return page.getContent().stream()
				.map(comment -> convertToCommentResDto(null, comment))
				.toList();
		}

		return page.getContent().stream()
			.map(comment -> convertToCommentResDto(authUser, comment))
			.toList();
	}

	private CommentResDto convertToCommentResDto(AuthUser authUser, Comment comment) {
		String profileImage = null;
		if (authUser != null) {
			profileImage = findProfileImageByUserEmail(authUser.email());
		}

		return new CommentResDto(
			comment.getId(),
			comment.getContent(),
			comment.getCreatedAt().toString(),
			isUserAuthor(authUser, comment),
			new CommentUserResDto(findUserNicknameByComment(comment), profileImage));
	}

	private boolean isUserAuthor(AuthUser authUser, Comment comment) {
		return (authUser != null) && comment.getUser().getEmail().equals(authUser.email());
	}

	private String findUserNicknameByComment(Comment comment) {
		return comment.getUser().getNickname();
	}

	private String findProfileImageByUserEmail(String email) {
		return profileImageRepository.findByUserEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_IMAGE_NOT_FOUND))
			.getProfileImage();
	}

	private PageInfo createPageInfo(Page<Comment> page, int pageIndex) {
		return new PageInfo(pageIndex, !page.hasNext());
	}

	private boolean isUserComment(Comment comment, String userEmail) {
		return comment.getUser().getEmail().equals(userEmail);
	}

	private boolean isCommentInUserPost(Long postId, String userEmail) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_POST_NOT_FOUND));

		return post.getUser().getEmail().equals(userEmail);
	}

	private Comment findCommentById(Long id) {
		return commentRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_COMMENT_NOT_FOUND));
	}

	private void validateDeletionRights(Comment comment, Long postId, AuthUser authUser) {
		boolean isUserComment = isUserComment(comment, authUser.email());
		boolean isCommentInUserPost = isCommentInUserPost(postId, authUser.email());

		if (!isUserComment && !isCommentInUserPost) {
			throw new UnauthorizedException(ErrorCode.FAIL_COMMENT_DELETION_FORBIDDEN);
		}
	}
}
