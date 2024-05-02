package com.thinktank.api.entity;

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
@Table(name = "tbl_user_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLike {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "like_id")
	private Like like;

	@Column(name = "is_check", nullable = false)
	private boolean isCheck;

	@Builder
	public UserLike(User user, Like like, boolean isCheck) {
		this.user = user;
		this.like = like;
		this.isCheck = isCheck;
	}

	public void activateLike() {
		this.isCheck = true;
	}

	public void deactivateLike() {
		this.isCheck = false;
	}
}
