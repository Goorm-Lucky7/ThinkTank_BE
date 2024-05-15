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
			"docker", "run", "-i", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + ":/app",
			"openjdk:17",
			"java", "-cp", "/app/" + directory.toString().replace("/tmp", ""), "Main"
		);
	}

	public static List<String> javaScriptCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + ":/app",
			"node:alpine",
			"node", "/app/" + directory.toString().replace("/tmp", "") + "/" + JAVASCRIPT_CLASS_NAME
		);
	}

	public static List<String> compileCommand(File sourceFile, String tempDirPath) {
		return Arrays.asList(
			"docker", "run", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + "/" +
				tempDirPath.toString().replace("/tmp", "") +
				":/app",
			"-w", "/app",
			"openjdk:17",
			"javac", sourceFile.getName()
		);
	}
}
