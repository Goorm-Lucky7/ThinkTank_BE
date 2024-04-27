package com.thinktank.api.service;

import org.springframework.stereotype.Service;

import com.thinktank.api.dto.comment.CommentCreateDto;
import com.thinktank.api.entity.Comment;
import com.thinktank.api.repository.CommentRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private static final int CONTENT_MAX_LENGTH = 100;

	private final CommentRepository commentRepository;

	@Transactional
	public Comment create(CommentCreateDto commentCreateDto){
		validateContentLength(commentCreateDto.content());

		Comment comment = Comment.create(commentCreateDto);

		return commentRepository.save(comment);
	}

	private void validateContentLength(String content){
		if(content.length() > CONTENT_MAX_LENGTH) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}
}
