package com.example.jarvisassistant

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class JarvisInteractionBoard {
    private val conversationHistory = mutableListOf<Pair<String, String>>()
    private val commands = mapOf(
        "你好" to ::greet,
        "时间" to ::getTime,
        "天气" to ::getWeather,
        "笑话" to ::tellJoke,
        "帮助" to ::getHelp
    )

    fun processCommand(text: String): String {
        val lowerText = text.toLowerCase()
        conversationHistory.add("user" to text)
        
        for ((key, func) in commands) {
            if (key in lowerText) {
                val response = func()
                conversationHistory.add("jarvis" to response)
                return response
            }
        }
        
        val response = "抱歉,我没有理解这个命令。你可以尝试说'帮助'来查看我能做什么。"
        conversationHistory.add("jarvis" to response)
        return response
    }

    private fun greet() = "你好！我是Jarvis,有什么我可以帮你的吗？"

    private fun getTime(): String {
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        return "现在的时间是 $currentTime"
    }

    private fun getWeather() = "抱歉,我目前无法获取实时天气数据。"

    private fun tellJoke(): String {
        val jokes = listOf(
            "为什么电脑会生病？因为它们有病毒！",
            "我有一个删除重力的笑话,但它可能会飞过去。",
            "为什么程序员更喜欢黑暗模式？因为光明会消耗他们的能量。"
        )
        return jokes[Random.nextInt(jokes.size)]
    }

    private fun getHelp() = "我可以帮你查看时间、讲笑话,或者问候你。只需要说出相关的关键词即可。"

    fun getConversationHistory() = conversationHistory.toList()
}

