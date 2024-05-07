package com.thinktank.api.dto.post.response;

public record PostProfileResponseDto(
	Long postId,
	Long postNumber,
	String title,
	String category,
	boolean likeType,
	int commentCount,
	int likeCount,
	int answerCount
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
