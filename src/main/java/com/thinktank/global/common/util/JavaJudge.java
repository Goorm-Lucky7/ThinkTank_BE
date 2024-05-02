package com.thinktank.global.common.util;

import static com.thinktank.global.common.util.DockerCommand.*;
import static com.thinktank.global.common.util.GlobalConstant.*;

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
			final File sourceFile = createFile(directory, code);

			startCompile(sourceFile, testCases, directory);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BadRequestException(ErrorCode.BAD_REQUEST_TIME_OUT);
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

	private void validateCompile(Process compileProcess) throws InterruptedException {
		if (compileProcess.waitFor() != ZERO) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST_COMPILE_ERROR);
		}
	}

	private void runTestCases(List<CustomTestCase> testCases, File tempDir) throws IOException {
		final ProcessBuilder builder = startDockerRun(tempDir);

		for (CustomTestCase testCase : testCases) {
			final Process process = builder.start();
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			writer.write(testCase.example() + "\n");
			writer.flush();

			final long startTime = System.currentTimeMillis();

			timeOutCheck(process, startTime);

			final String output = reader.readLine();

			validateJudge(testCase.result(), output);
		}
	}

	private void timeOutCheck(Process process, long startTime) {
		while (!isProcessCompleted(process)) {
			if (System.currentTimeMillis() - startTime > EXECUTION_TIME_LIMIT) {
				process.destroy();
				throw new BadRequestException(ErrorCode.BAD_REQUEST_TIME_OUT);
			}
		}
	}

	private boolean isProcessCompleted(Process process) {
		try {
			process.exitValue();
			return true;
		} catch (IllegalThreadStateException e) {
			return false;
		}
	}

	private static void validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST_FAIL);
		}
	}

	private ProcessBuilder startDockerCompile(File sourceFile, String tempDirPath) {
		return new ProcessBuilder(compileCommand(sourceFile, tempDirPath));
	}

	private File createFile(File tempDir, String code) {
		final File sourceFile = new File(tempDir, JAVA_CLASS_NAME);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(code);
		} catch (IOException e) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}

		return sourceFile;
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
