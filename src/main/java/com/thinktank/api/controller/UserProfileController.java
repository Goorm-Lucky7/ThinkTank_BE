package com.thinktank.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thinktank.api.dto.profileImage.request.ProfileImageReqDto;
import com.thinktank.api.dto.user.request.UserUpdateDto;
import com.thinktank.api.dto.user.response.UserResDto;
import com.thinktank.api.entity.auth.AuthUser;
import com.thinktank.api.service.UserProfileService;
import com.thinktank.api.service.UserService;
import com.thinktank.global.auth.annotation.Auth;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class UserProfileController {

	private final UserProfileService userProfileService;
	private final UserService userService;

	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<UserResDto> findUserProfile(@Auth AuthUser authUser) {
		return ResponseEntity.ok(userService.findUserProfile(authUser));
	}

	@PutMapping("/users/image")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> updateProfileImage(
		@Auth AuthUser authUser,
		@RequestBody @Validated ProfileImageReqDto profileImageReqDto) {
		userProfileService.updateProfileImage(authUser, profileImageReqDto);
		return ResponseEntity.ok("OK");
	}

	@PutMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> updateUserNickname(
		@Auth AuthUser authUser,
		@RequestBody @Validated UserUpdateDto userUpdateDto) {
		userService.updateUserNickname(authUser, userUpdateDto);
		return ResponseEntity.ok("OK");
	}
}
