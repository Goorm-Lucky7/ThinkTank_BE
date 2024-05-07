package com.thinktank.global.common.util;

import java.io.File;
import java.util.List;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;

public interface JudgeUtil {
	void executeCode(List<CustomTestCase> testCases, String code);

	ProcessBuilder startDockerRun(File tempDir);
}
