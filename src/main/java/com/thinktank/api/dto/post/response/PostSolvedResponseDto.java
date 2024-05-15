package com.thinktank.api.dto.post.response;

public record PostSolvedResponseDto(
	Long postId,
	Long postNumber,
	String language,
	String title,
	String code
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
