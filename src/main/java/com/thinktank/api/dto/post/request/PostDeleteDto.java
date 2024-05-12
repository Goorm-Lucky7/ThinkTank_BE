package com.thinktank.api.dto.post.request;

import jakarta.validation.constraints.NotNull;

public record PostDeleteDto(
	@NotNull(message = "[❎ ERROR] 삭제하고자 하는 게시글의 아이디 값을 입력해주세요.")
	Long postId
) {
}
