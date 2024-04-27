package com.thinktank.api.service;

import org.springframework.stereotype.Service;

import com.thinktank.api.dto.comment.request.CommentCreateDto;
import com.thinktank.api.entity.Comment;
import com.thinktank.api.entity.Post;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private static final int CONTENT_MAX_LENGTH = 100;

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	@Transactional
	public Comment create(CommentCreateDto commentCreateDto){
		Post post = postRepository.findById(commentCreateDto.postId())
				.orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST));
		validateContentLength(commentCreateDto.content());

		Comment comment = Comment.create(commentCreateDto, post);

		return commentRepository.save(comment);
	}

	private void validateContentLength(String content){
		if(content.length() > CONTENT_MAX_LENGTH) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}
}
