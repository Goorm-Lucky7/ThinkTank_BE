package com.thinktank.global.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {


	FAIL_INVALID_REQUEST("[❎ ERROR] 잘못된 요청입니다. 입력 형식을 확인해주세요."),
	FAIL_INCORRECT_PASSWORD("[❎ ERROR] 입력하신 비밀번호가 정확하지 않습니다. 다시 시도해 주세요."),
	FAIL_CATEGORY_NOT_FOUND("[❎ ERROR] 선택하신 카테고리가 유효하지 않습니다. 카테고리 목록을 확인해 주세요."),
	FAIL_UNSUPPORTED_LANGUAGE("[❎ ERROR] 지원하지 않는 언어입니다. 지원하는 언어 목록을 확인해 주세요."),
	FAIL_TOKEN_EXPIRED_OR_INVALID("[❎ ERROR] 인증 토큰이 유효하지 않습니다. 다시 로그인해 주세요."),
	FAIL_CODE_COMPILATION_ERROR("[❎ ERROR] 코드 컴파일 중 에러가 발생했습니다. 코드를 확인해주세요."),
	FAIL_TESTCASE_NOT_PASSED("[❎ ERROR] 하나 이상의 테스트케이스를 통과하지 못했습니다. 코드를 재검토해 주세요."),
	FAIL_PROCESSING_TIME_EXCEEDED("[❎ ERROR] 처리 시간이 초과되었습니다. 요청을 다시 시도하거나, 처리량을 줄여 주세요."),
	FAIL_IMAGE_ALREADY_SET("[❎ ERROR] 선택하신 이미지는 이미 기본 프로필 이미지로 설정되어 있습니다. 다른 이미지를 선택해 주세요."),


	FAIL_LOGIN_REQUIRED("[❎ ERROR] 로그인이 필요한 기능입니다."),
	FAIL_TOKEN_EXPIRED("[❎ ERROR] 인증 토큰이 만료되었습니다. 다시 로그인해 주세요."),
	FAIL_INVALID_TOKEN("[❎ ERROR] 유효하지 않은 인증 토큰입니다. 다시 로그인해 주세요."),


	FAIL_POST_CREATION_FORBIDDEN("[❎ ERROR] 게시글 작성 권한이 없습니다."),
	FAIL_POST_DELETION_FORBIDDEN("[❎ ERROR] 게시글 삭제 권한이 없습니다."),
	FAIL_COMMENT_CREATION_FORBIDDEN("[❎ ERROR] 댓글 작성 권한이 없습니다."),
	FAIL_COMMENT_DELETION_FORBIDDEN("[❎ ERROR] 댓글 삭제 권한이 없습니다."),


	FAIL_POST_NOT_FOUND("[❎ ERROR] 요청하신 게시글을 찾을 수 없습니다."),
	FAIL_USER_NOT_FOUND("[❎ ERROR] 요청하신 회원을 찾을 수 없습니다."),
	FAIL_COMMENT_NOT_FOUND("[❎ ERROR] 요청하신 댓글을 찾을 수 없습니다."),
	FAIL_TOKEN_NOT_FOUND("[❎ ERROR] 요청하신 토큰을 찾을 수 없습니다."),
	FAIL_LANGUAGE_NOT_FOUND("[❎ ERROR] 요청하신 언어를 찾을 수 없습니다."),
	FAIL_IMAGE_NOT_FOUND("[❎ ERROR] 요청하신 이미지를 찾을 수 없습니다."),


	FAIL_EMAIL_CONFLICT("[❎ ERROR] 이미 존재하는 이메일입니다."),
	FAIL_NICKNAME_CONFLICT("[❎ ERROR] 이미 존재하는 닉네임입니다."),
	FAIL_USER_LIKE_NOT_FOUND("[❎ ERROR] 좋아요를 누른 회원을 찾을 수 없습니다."),
	FAIL_LIKE_NOT_FOUND("[❎ ERROR] 존재하지 않는 좋아요입니다.");

	private String message;
}
