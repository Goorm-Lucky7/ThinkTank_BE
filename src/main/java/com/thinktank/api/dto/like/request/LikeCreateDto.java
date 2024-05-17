package com.thinktank.api.dto.like.request;

import jakarta.validation.constraints.NotNull;

public record LikeCreateDto(
	@NotNull (message = "[❎ ERROR] 좋아요를 누르려고 하는 게시글 아이디 값을 입력해주세요.")
	Long postId) {
}
