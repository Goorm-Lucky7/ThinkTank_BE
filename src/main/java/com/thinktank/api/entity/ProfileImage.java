package com.thinktank.api.entity;

import com.thinktank.api.dto.user.request.UserUpdateDto;

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
@Table(name = "tbl_profile_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "profile_image")
	private String profileImage;

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public ProfileImage(String profileImage, User user) {
		this.profileImage = profileImage;
		this.user = user;
	}

	public static ProfileImage createDefaultForUser(User user) {
		return ProfileImage.builder()
			.profileImage(null)
			.user(user)
			.build();
	}

	public void updateProfileImage(UserUpdateDto userUpdateDto) {
		this.profileImage = userUpdateDto.profileImage();
	}
}
