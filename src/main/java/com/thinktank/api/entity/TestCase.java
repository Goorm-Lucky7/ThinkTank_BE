package com.thinktank.api.entity;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_test_case")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCase {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "example", nullable = false)
	private String example;

	@Column(name = "result", nullable = false)
	private String result;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Builder
	private TestCase(
		String example,
		String result,
		Post post
	) {
		this.example = example;
		this.result = result;
		this.post = post;
	}

	public static TestCase createTestCase(CustomTestCase customTestCase, Post post) {
		return TestCase.builder()
			.example(customTestCase.example())
			.result(customTestCase.result())
			.post(post)
			.build();
	}
}
