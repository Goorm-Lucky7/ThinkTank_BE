package com.thinktank.global.common.util;

import static com.thinktank.global.common.util.DockerCommand.*;
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
public class JavaJudge implements JudgeUtil {
	@Override
	public void executeCode(List<CustomTestCase> testCases, String code) {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File(uniqueDirName);

		validateExist(directory);
		try {
			final File sourceFile = createFile(directory, code, testCases.size());

			startCompile(sourceFile, testCases, directory);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BadRequestException(ErrorCode.FAIL_TIME_OUT);
		} finally {
			delete(directory);
		}
	}

	@Override
	public ProcessBuilder startDockerRun(File tempDir) {
		return new ProcessBuilder(javaCommand(tempDir)).redirectErrorStream(true);
	}

	private void startCompile(
		File sourceFile,
		List<CustomTestCase> testCases,
		File tempDir
	) throws InterruptedException, IOException {
		final String tempDirPath = tempDir.getAbsolutePath();
		final Process compileProcess = startDockerCompile(sourceFile, tempDirPath).start();

		validateCompile(compileProcess);
		runTestCases(testCases, tempDir);
		Thread.currentThread().interrupt();
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

	private ProcessBuilder startDockerCompile(File sourceFile, String tempDirPath) {
		return new ProcessBuilder(compileCommand(sourceFile, tempDirPath));
	}

	private File createFile(File tempDir, String code, int size) throws IOException {
		final File sourceFile = new File(tempDir, JAVA_CLASS_NAME);
		final String codeWithLoop = String.format(JAVA_TEMPLATE, size, code);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(codeWithLoop);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}

		return sourceFile;
	}

	private void validateCompile(Process compileProcess) throws InterruptedException {
		if (compileProcess.waitFor() != ZERO) {
			throw new BadRequestException(ErrorCode.FAIL_COMPILE_ERROR);
		}
	}

	private static void validateTimeOut(long currentTime, long startTime) {
		if (currentTime - startTime > EXECUTION_TIME_LIMIT) {
			throw new BadRequestException(ErrorCode.FAIL_TIME_OUT);
		}
	}

	private static void validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			throw new BadRequestException(ErrorCode.FAIL_TESTCASES);
		}
	}

	private void validateExist(File tempDir) {
		if (!tempDir.mkdirs()) {
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
}
