package com.thinktank.api.dto.problemType.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProblemTypeDto(
	@NotBlank(message = "[❎ ERROR] created, solved, liked 중 원하는 값을 넘겨주세요.")
	String value,
	@NotNull(message = "[❎ ERROR] 조회하고자 하는 유저의 아이디 값을 넣어주세요.")
	Long userId
) {
}
