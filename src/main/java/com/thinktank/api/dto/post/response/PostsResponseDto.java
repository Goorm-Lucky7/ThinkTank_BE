package com.thinktank.api.dto.post.response;

import java.time.LocalDateTime;

import com.thinktank.api.dto.user.response.SimpleUserResDto;

public record PostsResponseDto(
	Long postId,
	Long postNumber,
	String title,
	String category,
	LocalDateTime createdAt,
	String content,
	int commentCount,
	int likeCount,
	int codeCount,
	boolean likeType,
	SimpleUserResDto user
) {
}
