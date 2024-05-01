package com.thinktank.global.common.util;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.thinktank.api.dto.testcase.custom.TestCaseDto;
import com.thinktank.global.error.exception.BadRequestException;
import com.thinktank.global.error.model.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaScriptJudge implements JudgeUtil {
	@Override
	public void executeCode(List<TestCaseDto> testCases, String code) {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File(uniqueDirName);

		validateExist(directory);
		createFile(directory, getUserCode());
		try {
			runTestCases(testCases, directory);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		} finally {
			delete(directory);
		}
	}

	@Override
	public ProcessBuilder startDockerRun(File tempDir) {
		List<String> command = new ArrayList<>(Arrays.asList(
			"docker", "run", "--rm", "-i",
			"-v", tempDir.getAbsolutePath() + ":/app",
			"node:alpine",
			"node", "/app/" + JAVASCRIPT_CLASS_NAME
		));

		return new ProcessBuilder(command).redirectErrorStream(true);
	}

	private void runTestCases(List<TestCaseDto> testCases, File tempDir) throws IOException {
		for (TestCaseDto testCase : testCases) {
			final ProcessBuilder builder = startDockerRun(tempDir);
			final Process process = builder.start();

			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
				writer.write(testCase.example() + "\n");
				writer.flush();
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String output = reader.readLine();
				validateJudge(testCase.result(), output);
			}
		}
	}

	private static void validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST_FAIL);
		}
	}

	private void validateExist(File tempDir) {
		if (!tempDir.mkdirs()) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private void createFile(File tempDir, String code) {
		final File sourceFile = new File(tempDir, JAVASCRIPT_CLASS_NAME);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(code);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
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

	private String getUserCode() {
		return "const readline = require('readline').createInterface({\n" +
			"  input: process.stdin,\n" +
			"  output: process.stdout\n" +
			"});\n" +
			"\n" +
			"readline.question('', input => {\n" +
			"  const [a, b] = input.split(' ').map(Number); // 공백으로 분리하여 입력 받기\n" +
			"  console.log(a + b);\n" +
			"  readline.close();\n" +
			"});\n";
	}
}
