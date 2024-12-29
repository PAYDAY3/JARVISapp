package com.example.jarvisassistant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;

public class JarvisUtils {
    private static Map<String, Method> javaTools = new HashMap<>();
    private static Map<String, String> toolNames = new HashMap<>();

    static {
        scanJavaTools();
    }

    private static void scanJavaTools() {
        Method[] methods = JarvisUtils.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("tool_")) {
                javaTools.put(method.getName(), method);
                toolNames.put(method.getName(), method.getName().substring(5).replace('_', ' '));
            }
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    public static boolean isPalindrome(String input) {
        String cleaned = input.replaceAll("\\s+", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    public static String caesarCipher(String input, int shift) {
        StringBuilder result = new StringBuilder();
        for (char character : input.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isUpperCase(character) ? 'A' : 'a';
                result.append((char) ((character - base + shift) % 26 + base));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String tool_current_time() {
        return "当前时间: " + getCurrentTime();
    }

    public static String tool_current_date() {
        return "当前日期: " + getCurrentDate();
    }

    public static String tool_random_number() {
        return "随机数 (1-100): " + generateRandomNumber(1, 100);
    }

    public static String executeJavaTool(String toolName) {
        try {
            Method method = javaTools.get(toolName);
            if (method != null) {
                return (String) method.invoke(null);
            } else {
                for (Map.Entry<String, String> entry : toolNames.entrySet()) {
                    if (entry.getValue().equals(toolName)) {
                        method = javaTools.get(entry.getKey());
                        return (String) method.invoke(null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知的Java工具: " + toolName;
    }

    public static void addTool(String name, Method method) {
        javaTools.put(method.getName(), method);
        toolNames.put(method.getName(), name);
    }

    public static String modifyToolName(String oldName, String newName) {
        for (Map.Entry<String, String> entry : toolNames.entrySet()) {
            if (entry.getValue().equals(oldName)) {
                toolNames.put(entry.getKey(), newName);
                return "工具名称已修改: " + oldName + " -> " + newName;
            }
        }
        return "未找到工具: " + oldName;
    }

    public static List<String> getToolList() {
        return new ArrayList<>(toolNames.values());
    }
}

