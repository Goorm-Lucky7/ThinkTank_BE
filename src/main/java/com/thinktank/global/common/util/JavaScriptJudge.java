package com.thinktank.global.common.util;

import static com.thinktank.global.common.util.GlobalConstant.*;
import static com.thinktank.global.common.util.Template.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import com.thinktank.api.dto.testcase.custom.CustomTestCase;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaScriptJudge implements JudgeUtil {

	@Override
	public void executeCode(List<CustomTestCase> testCases, String code) {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File(uniqueDirName);

		validateExist(directory);
		try {
			createFile(directory, code, testCases.size());
			runTestCases(testCases, directory);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_REQUEST);
		} finally {
			delete(directory);
		}
	}

	@Override
	public ProcessBuilder startDockerRun(File tempDir) {
		return new ProcessBuilder(DockerCommand.javaScriptCommand(tempDir)).redirectErrorStream(true);
	}

	private void runTestCases(List<CustomTestCase> testCases, File tempDir) throws IOException {
		final ProcessBuilder builder = startDockerRun(tempDir);
		final long startTime = System.currentTimeMillis();
		final Process process = builder.start();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		for (CustomTestCase testCase : testCases) {
			writer.write(testCase.example() + "\n");
			writer.flush();

			final String output = reader.readLine();
			final long currentTime = System.currentTimeMillis();

			validateTimeOut(currentTime, startTime);
			validateJudge(testCase.result(), output);
		}
	}

	private static void validateTimeOut(long currentTime, long startTime) {
		if (currentTime - startTime > EXECUTION_TIME_LIMIT) {
			throw new BadRequestException(ErrorCode.FAIL_PROCESSING_TIME_EXCEEDED);
		}
	}

	private static void validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			throw new BadRequestException(ErrorCode.FAIL_TESTCASE_NOT_PASSED);
		}
	}

	private void validateExist(File tempDir) {
		if (!tempDir.mkdirs()) {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_REQUEST);
		}
	}

	private void createFile(File tempDir, String code, int size) throws IOException {
		final File sourceFile = new File(tempDir, JAVASCRIPT_CLASS_NAME);
		final String codeWithLoop = String.format(JAVASCRIPT_TEMPLATE, size, code);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(codeWithLoop);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.FAIL_INVALID_REQUEST);
		}
	}

	private void delete(File directory) {
		final File[] allContents = directory.listFiles();

		if (allContents != null) {
			for (File file : allContents) {
				delete(file);
			}
		}

		directory.delete();
	}
}
