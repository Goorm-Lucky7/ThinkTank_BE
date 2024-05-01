package com.thinktank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.Post;
import com.thinktank.api.entity.TestCase;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
	List<TestCase> findByPost(Post post);
}
