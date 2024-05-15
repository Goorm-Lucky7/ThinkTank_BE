package com.thinktank.api.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Language {
	Java("자바", "java"),
	Javascript("자바스크립트", "javascript");

	private final String name;
	private final String value;

	Language(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private static Map<String, Language> names = new HashMap<>();
	private static Map<String, Language> valuesMap = new HashMap<>();

	static {
		for (Language language : Language.values()) {
			names.put(language.name, language);
			valuesMap.put(language.value, language);
		}

		names = Collections.unmodifiableMap(names);
		valuesMap = Collections.unmodifiableMap(valuesMap);
	}

	private String getValue() {
		return value;
	}

	public static Language fromValue(String value) {
		return valuesMap.get(value);
	}

	public static boolean isValidLanguage(String value) {
		for (Language language : Language.values()) {
			if (language.getValue().equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}
}
