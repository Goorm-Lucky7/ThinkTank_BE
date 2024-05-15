package com.thinktank.api.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Category {
	DFS("깊이우선탐색", "dfs"),
	BFS("너비우선탐색", "bfs"),
	SelectionSort("선택 정렬", "SelectionSort"),
	QuickSort("퀵 정렬", "QuickSort"),
	MergeSort("병합 정렬", "MergeSort"),
	HeapSort("힙 정렬", "HeapSort"),
	RadixSort("기수 정렬", "RadixSort"),
	BinarySearch("이진 탐색", "BinarySearch"),
	LinearSearch("선형 탐색", "LinearSearch"),
	FibonacciSequence("피보나치 수열", "FibonacciSequence"),
	KnapsackProblem("배낭 문제", "KnapsackProblem"),
	LongestCommonSubsequence("최장 공통 부분 수열", "LongestCommonSubsequence"),
	LongestIncreasingSubsequence("최장 증가 부분 수열", "LongestIncreasingSubsequence"),
	CoinChangeProblem("동전 교환 문제", "CoinChangeProblem"),
	NQueensProblem("N-퀸 문제", "NQueensProblem"),
	HamiltonianPaths("해밀턴 경로", "HamiltonianPaths"),
	PermutationsGeneration("순열 생성", "PermutationsGeneration"),
	EuclideanAlgorithm("유클리드 알고리즘", "EuclideanAlgorithm"),
	PrimeNumberChecking("소수 판별", "PrimeNumberChecking"),
	MatrixOperations("행렬 연산", "MatrixOperations"),
	ConvexHull("볼록 껍질", "ConvexHull"),
	LineSegmentIntersection("선분 교차", "LineSegmentIntersection"),
	ClosestPairofPoints("최근접 점 쌍", "ClosestPairofPoints");

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
