package com.thinktank.api.dto.post.response;

import java.time.LocalDateTime;

public record PostProfileResponseDto(
	Long postId,
	Long postNumber,
	String title,
	String category,
	LocalDateTime createdAt,
	String content,
	boolean likeType,
	int commentCount,
	int likeCount,
	int codeCount
) implements PostResponseDto {
	@Override
	public Long getPostId() {
		return postId;
	}

	@Override
	public Long getPostNumber() {
		return postNumber;
	}

	@Override
	public String getTitle() {
		return title;
	}
}
