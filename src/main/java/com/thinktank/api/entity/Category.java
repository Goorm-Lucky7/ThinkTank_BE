package com.thinktank.api.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
	DFS("깊이우선탐색","dfs"),
	BFS("너비우선탐색","bfs");
	private final String name;
	private final String value;
}
