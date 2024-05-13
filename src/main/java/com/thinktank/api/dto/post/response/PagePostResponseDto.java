package com.thinktank.api.dto.post.response;

import java.util.List;

import com.thinktank.api.dto.page.response.PageInfo;

public record PagePostResponseDto(
	List<PostsResponseDto> posts,
	PageInfo pageInfo
) {
}
