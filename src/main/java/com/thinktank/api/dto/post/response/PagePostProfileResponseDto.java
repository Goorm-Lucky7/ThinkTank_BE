package com.thinktank.api.dto.post.response;

import java.util.List;

import com.thinktank.api.dto.page.response.PageInfo;
import com.thinktank.api.dto.user.response.UserProfileResDto;

public record PagePostProfileResponseDto(
	UserProfileResDto user,
	List<? extends PostResponseDto> posts,
	PageInfo pageInfo
) {
}
