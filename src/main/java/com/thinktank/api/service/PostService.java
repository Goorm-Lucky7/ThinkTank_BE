package com.thinktank.api.service;

import org.springframework.stereotype.Service;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.entity.Post;
import com.thinktank.api.repository.PostRepository;

import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;

	public Post createPost(PostCreateDto postCreateDto) {
		validatePostTitleLength(postCreateDto);
		Post post = Post.create(postCreateDto);
		return postRepository.save(post);
	}

	private void validatePostTitleLength(PostCreateDto postCreateDto) {
		if (postCreateDto.title().length()>20) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

}
