package com.thinktank.global.common.util;

public class Template {
	private Template() {
	}

	public static final String JAVA_TEMPLATE =
		"import java.io.*;\n" +
			"import java.util.*;\n" +
			"import java.util.stream.*;\n" +

			"public class Main {\n" +
			"    public static void main(String[] args) throws IOException {\n" +
			"    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));" +
			"        for(int i = 0; i < %d; i++) {\n" +
			"			 String qwertasdf = br.readLine();   \n" +
			"            System.out.println(solution(qwertasdf));\n" +
			"        }\n" +
			"    }\n" +
			"    %s\n" +
			"}\n";
}
