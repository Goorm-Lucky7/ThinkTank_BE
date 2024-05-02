package com.thinktank.api.dto.profileImage.request;

import jakarta.validation.constraints.Pattern;

public record ProfileImageReqDto(
	String fileName,
	@Pattern(regexp = "([^\\s]+(\\.(?i)(jpg|png|jpeg))$)", message = "[❎ ERROR] 확장자는 png, jpg, jpeg만 가능합니다.")
	String fileUrl,
	String originalFileName
) {
}
