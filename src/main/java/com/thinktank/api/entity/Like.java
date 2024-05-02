package com.thinktank.api.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "like_count", nullable = false)
	@ColumnDefault("1")
	private int likeCount = 1;

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "post_id")
	private Post post;

	@Builder
	public Like(Post post) {
		this.post = post;
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		this.likeCount--;
	}
}
