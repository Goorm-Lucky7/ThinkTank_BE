package com.thinktank.api.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Category {
	DFS("깊이우선탐색", "dfs"),
	BFS("너비우선탐색", "bfs");

	private final String name;
	private final String value;

	Category(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private static Map<String, Category> names = new HashMap<>();
	private static Map<String, Category> valuesMap = new HashMap<>();

	static {
		for (Category category : Category.values()) {
			names.put(category.name, category);
			valuesMap.put(category.value, category);
		}
		names = Collections.unmodifiableMap(names);
		valuesMap = Collections.unmodifiableMap(valuesMap);
	}

	private String getValue() {
		return value;
	}

	public static Category fromValue(String value) {
		return valuesMap.get(value);
	}

	public static boolean isValidCategory(String value) {
		for (Category category : Category.values()) {
			if (category.getValue().equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}
}
