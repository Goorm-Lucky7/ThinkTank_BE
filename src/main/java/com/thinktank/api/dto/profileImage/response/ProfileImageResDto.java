package com.thinktank.api.dto.profileImage.response;

public record ProfileImageResDto(
	String fileName,
	String fileUrl,
	String originalFileName
) {
}
