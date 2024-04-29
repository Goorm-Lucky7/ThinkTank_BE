package com.thinktank.global.common.util;

import java.util.List;

import com.thinktank.api.dto.testcase.custom.TestCaseDto;

public interface JudgeUtil {
	void executeCode(List<TestCaseDto> testCases, String code);
}
