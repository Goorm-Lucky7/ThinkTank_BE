package com.thinktank.api.dto.post.response;

import java.util.List;

import com.thinktank.api.dto.page.response.PageInfoDto;

public record PagePostResponseDto(
	List<PostsResponseDto> posts,
	PageInfoDto pageInfoDto
) {
}
