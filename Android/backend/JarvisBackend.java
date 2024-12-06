package com.example.jarvisassistant.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JarvisBackend {
    private static Map<String, String> pythonScripts = new HashMap<>();

    public static String processCommand(String text) {
        text = text.toLowerCase();
        if (text.contains("你好") || text.contains("嗨")) {
            return "你好！我是Jarvis，有什么我可以帮你的吗？";
        } else if (text.contains("时间")) {
            return "现在的时间是 " + new Date().toString();
        } else if (text.contains("天气")) {
            return "抱歉，我目前无法获取实时天气数据。";
        } else if (text.contains("python") || text.contains("脚本")) {
            return "我可以运行Python脚本。请指定要运行的脚本名称。";
        } else {
            for (String key : pythonScripts.keySet()) {
                if (text.contains(key.toLowerCase())) {
                    return runPythonScript(key);
                }
            }
            return "抱歉，我没有理解这个命令。你能换个方式问或者问点别的吗？";
        }
    }

    public static void addPythonScript(String name, String script) {
        pythonScripts.put(name, script);
    }

    private static String runPythonScript(String name) {
        String script = pythonScripts.get(name);
        if (script == null) {
            return "找不到指定的Python脚本。";
        }

        try {
            Process p = Runtime.getRuntime().exec("python -c \"" + script + "\"");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (Exception e) {
            return "运行Python脚本时出错: " + e.getMessage();
        }
    }
}
