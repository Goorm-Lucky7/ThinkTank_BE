package com.thinktank.global.common.util;

import static com.thinktank.global.common.util.GlobalConstant.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerCommand {
	public static List<String> javaCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "--rm", "-i",
			"-v", directory.getAbsolutePath() + ":/app",
			"openjdk:17",
			"sh", "-c", "cd /app && java " + FULL_CLASS_NAME
		);
	}

	public static List<String> javaScriptCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "--rm", "-i",
			"-v", directory.getAbsolutePath() + ":/app",
			"node:alpine",
			"node", "/app/" + JAVASCRIPT_CLASS_NAME
		);
	}

	public static List<String> compileCommand(File sourceFile, String tempDirPath) {
		return Arrays.asList(
			"docker", "run", "--rm",
			"-v", tempDirPath + ":/usr/src/myapp",
			"-w", "/usr/src/myapp",
			"openjdk:17",
			"javac", sourceFile.getName()
		);
	}
}
