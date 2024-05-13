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
		"const { Worker, isMainThread } = require('worker_threads');\n" +
			"if (isMainThread) {\n" +
			"    const worker = new Worker(__filename);\n" +
			"    const readline = require('readline').createInterface({\n" +
			"        input: process.stdin,\n" +
			"    });\n" +
			"    let inputCount = 0;\n" +
			"    const getInput = () => {\n" +
			"        readline.question('Enter two numbers separated by space: ', (input) => {\n" +
			"            worker.postMessage(input);\n" +
			"            inputCount++;\n" +
			"            if (inputCount === 1) {\n" +
			"                setTimeout(() => {\n" +
			"                    worker.terminate().then(() => {\n" +
			"                        console.log('15 seconds');\n" +
			"                        process.exit();\n" +
			"                    });\n" +
			"                }, 15000);\n" +
			"            }\n" +
			"            if (inputCount < %d) {\n" +
			"                getInput();\n" +
			"            } else {\n" +
			"                readline.close();\n" +
			"            }\n" +
			"        });\n" +
			"    };\n" +
			"    getInput();\n" +
			"    worker.on('message', (result) => {\n" +
			"        console.log(result);\n" +
			"    });\n" +
			"    readline.on('close', () => {\n" +
			"    });\n" +
			"} else {\n" +
			"    const { parentPort } = require('worker_threads');\n" +
			"    parentPort.on('message', (input) => {\n" +
			"        const result = solution(input);\n" +
			"        parentPort.postMessage(result);\n" +
			"    });\n" +
			"%s" +
			"}";
}
