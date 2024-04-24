package com.thinktank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinktank.api.entity.TestCase;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}
