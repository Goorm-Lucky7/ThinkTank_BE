package com.thinktank.global.common.util;

public class Template {
	private Template() {
	}

	public static final String JAVA_TEMPLATE =
		"import java.io.*;\n" +
			"import java.util.*;\n" +
			"import java.util.stream.*;\n" +
			"import java.util.concurrent.*;\n" +

			"public class Main {\n" +
			"    public static void main(String[] args) throws IOException {\n" +
			"    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();\n" +
			"    executor.schedule(() -> System.exit(0), 15, TimeUnit.SECONDS);\n" +
			"    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));" +
			"        for(int zxc = 0; zxc < %d; zxc++) {\n" +
			"			 String qwer = br.readLine();   \n" +
			"            System.out.println(solution(qwer));\n" +
			"        }\n" +
			"    }\n" +
			"    %s\n" +
			"}\n";

	public static final String JAVASCRIPT_TEMPLATE =
		"const readline = require('readline').createInterface({\n" +
			"    input: process.stdin,\n" +
			"});\n\n" +
			"let zxcvbn = 0;\n" +
			"readline.on('line', input => {\n" +
			"console.log(solution(input));\n" +
			"zxcvbn++;\n" +
			"if (zxcvbn === %d) {\n" +
			"readline.close();\n" +
			"  }\n" +
			"});\n" +
			"readline.on('close', () => {\n" +
			"process.exit(0);\n" +
			"});\n" +
			"%s";
}
