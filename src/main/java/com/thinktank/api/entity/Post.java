package com.thinktank.api.entity;

import org.hibernate.annotations.ColumnDefault;

import com.thinktank.api.dto.post.request.PostCreateDto;
import com.thinktank.global.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tbl_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "`condition`", nullable = false)
	private String condition;

	@Enumerated(EnumType.STRING)
	@Column(name = "language", nullable = false)
	private Language language;

	@Column(name = "comment_count", nullable = false)
	@ColumnDefault("0")
	private int commentCount = 0;

	@Column(name = "answer_count", nullable = false)
	@ColumnDefault("0")
	private int answerCount = 0;

	@Column(name = "code", nullable = false)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	private Post(
		String title,
		Category category,
		Language language,
		String content,
		String condition,
		String code,
		User user
	) {
		this.title = title;
		this.category = category;
		this.language = language;
		this.content = content;
		this.condition = condition;
		this.code = code;
		this.user = user;
	}

	public static Post create(PostCreateDto postCreateDto, User user) {

		return Post.builder()
			.title(postCreateDto.title())
			.category(Category.fromValue(postCreateDto.category()))
			.language(Language.fromValue(postCreateDto.language()))
			.content(postCreateDto.content())
			.condition(postCreateDto.condition())
			.code(postCreateDto.code())
			.user(user)
			.build();
	}
}
