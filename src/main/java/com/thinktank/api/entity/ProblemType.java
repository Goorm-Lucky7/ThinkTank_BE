package com.thinktank.api.entity;

import java.util.HashMap;
import java.util.Map;

public enum ProblemType {
	CREATED("만든문제", "created"),
	SOLVED("맞은문제", "solved"),
	LIKED("좋아요 한 문제", "liked");

	private final String name;
	private final String value;

	private static final Map<String, ProblemType> nameMap = new HashMap<>();
	private static final Map<String, ProblemType> valueMap = new HashMap<>();

	static {
		for (ProblemType type : ProblemType.values()) {
			nameMap.put(type.name, type);
			valueMap.put(type.value, type);
		}
	}

	public String getValue() {
		return value;
	}

	public static ProblemType fromValue(String value) {
		return valueMap.get(value);
	}

	ProblemType(String name, String value) {
		this.name = name;
		this.value = value;
	}
}