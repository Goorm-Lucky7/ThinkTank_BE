package com.thinktank.global.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

	// 204: NO CONTENT

	// 400: BAD REQUEST
	BAD_REQUEST("[❎ ERROR]"),
	FAIL_WRONG_PASSWORD("[❎ ERROR] 비밀번호가 일치하지 않습니다."),
	FAIL_INVALID_CATEGORY("[❎ ERROR] 유효하지 않은 카테고리입니다."),
	FAIL_INVALID_LANGUAGE("[❎ ERROR] 유효하지 않은 언어입니다."),
	FAIL_INVALID_TOKEN("[❎ ERROR] 유효하지 않은 토큰입니다."),
	BAD_REQUEST_COMPILE_ERROR("[❎실패] 컴파일 에러 입니다."),
	BAD_REQUEST_RUNTIME_ERROR("[❎실패] 런타임 에러 입니다."),
	BAD_REQUEST_FAIL("[❎실패] 테스트케이스를 통과하지 못했습니다."),
	BAD_REQUEST_TIME_OUT("[❎실패] 시간 초과입니다."),

	// 401: UNAUTHORIZED
	FAIL_UNAUTHORIZED_EXCEPTION("[❎ ERROR] 로그인이 필요한 기능입니다."),
	FAIL_TOKEN_EXPIRED_EXCEPTION("[❎ ERROR] 인증 토큰이 만료되었습니다."),
	FAIL_INVALID_TOKEN_EXCEPTION("[❎ ERROR] 유효하지 않은 인증 토큰입니다."),

	// 403: FORBIDDEN
	POST_POST_FORBIDDEN_EXCEPTION("[❎ ERROR] 게시글 작성 권한이 없습니다."),
	DELETE_POST_FORBIDDEN_EXCEPTION("[❎ ERROR] 게시글 삭제 권한이 없습니다."),
	POST_COMMENT_FORBIDDEN_EXCEPTION("[❎ ERROR] 댓글 작성 권한이 없습니다."),
	DELETE_COMMENT_FORBIDDEN_EXCEPTION("[❎ ERROR] 댓글 삭제 권한이 없습니다."),

	// 404: NOT FOUNT
	FAIL_NOT_POST_FOUND_EXCEPTION("[❎ ERROR] 요청하신 게시물을 찾을 수 없습니다."),
	FAIL_NOT_USER_FOUND_EXCEPTION("[❎ ERROR] 요청하신 회원을 찾을 수 없습니다."),
	FAIL_NOT_COMMENT_FOUND_EXCEPTION("[❎ ERROR] 요청하신 댓글을 찾을 수 없습니다."),
	FAIL_NOT_TOKEN_FOUND_EXCEPTION("[❎ ERROR] 요청하신 토큰을 찾을 수 없습니다."),
	FAIL_NOT_COOKIE_FOUND_EXCEPTION("[❎ ERROR] 요청하신 쿠키를 찾을 수 없습니다."),
	FAIL_NOT_LANGUAGE_EXCEPTION("[❎ ERROR] 요청하신 언어를 찾을 수 없습니다."),
	FAIL_NOT_IMAGE_EXCEPTION("[❎ ERROR] 요청하신 이미지를 찾을 수 없습니다."),

	// 409: CONFLICT
	FAIL_EMAIL_CONFLICT("[❎ ERROR] 이미 존재하는 이메일입니다."),
	FAIL_NICKNAME_CONFLICT("[❎ ERROR] 이미 존재하는 닉네임입니다."),
	FAIL_NOT_USER_LIKE_FOUND_EXCEPTION("[❎ ERROR] 좋아요를 누른 회원을 찾을 수 없습니다."),
	FAIL_NOT_LIKE_FOUND_EXCEPTION("[❎ ERROR] 존재하지 않는 좋아요입니다.");

	private String message;
}
