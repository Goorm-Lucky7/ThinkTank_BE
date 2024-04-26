package com.thinktank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.api.entity.Category;
import com.thinktank.api.entity.Language;
import com.thinktank.api.entity.Post;
import com.thinktank.api.repository.PostRepository;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;

	public void createPost(PostCreateDto postCreateDto) {
		validateCategory(postCreateDto.category());
		validateLanguage(postCreateDto.language());
		Post post = Post.create(postCreateDto);
		postRepository.save(post);
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
