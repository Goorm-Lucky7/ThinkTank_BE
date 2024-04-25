package com.thinktank.api.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Language {
	JAVA("자바","java"),
	JAVASCRIPT("자바스크립트","javascript");
	private final String name;
	private final String value;
}
