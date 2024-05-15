package com.thinktank.api.dto.post.response;

import java.time.LocalDateTime;
import java.util.List;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

public record PostDetailResponseDto(
	Long postId,
	Long postNumber,
	String title,
	String category,
	LocalDateTime createdAt,
	String content,
	List<CustomTestCase> testCases,
	String condition,
	boolean isAuthor,
	int likeCount,
	int commentCount,
	int codeCount,
	String language,
	boolean likeType,
	String code
) {
}
