package com.example.jarvisassistant

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File

class PythonScriptManager(private val context: Context) {
    private val PYTHON_SCRIPTS_DIR = "python_scripts"
    private val python: Python

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
        python = Python.getInstance()
        loadPythonScripts()
    }

    private fun loadPythonScripts() {
        val scriptsDir = File(context.filesDir, PYTHON_SCRIPTS_DIR)
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs()
        }

        scriptsDir.listFiles { _, name -> name.endsWith(".py") }?.forEach { scriptFile ->
            try {
                val scriptContent = scriptFile.readText()
                val scriptName = scriptFile.nameWithoutExtension
                python.getModule("builtins").callAttr("exec", scriptContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun executeScript(scriptName: String, vararg args: Any): Any? {
        val module = python.getModule(scriptName)
        return module.callAttr("main", *args)
    }

    fun getAvailableScripts(): List<String> {
        val scriptsDir = File(context.filesDir, PYTHON_SCRIPTS_DIR)
        return scriptsDir.listFiles { _, name -> name.endsWith(".py") }
            ?.map { it.nameWithoutExtension } ?: emptyList()
    }
}

