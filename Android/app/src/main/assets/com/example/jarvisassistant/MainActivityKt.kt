package com.example.jarvisassistant

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.util.*

class MainActivityKt : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var voiceButton: Button
    private lateinit var menuButton: Button
    private lateinit var chatView: TextView
    private lateinit var pythonCodeView: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var inputProcessor: PyObject
    private val REQUEST_CODE_SPEECH_INPUT = 1000
    private val PYTHON_SCRIPTS_DIR = "python_scripts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        initTextToSpeech()
        initPython()
        loadPythonScripts()
    }

    private fun initViews() {
        inputText = findViewById(R.id.inputText)
        sendButton = findViewById(R.id.sendButton)
        voiceButton = findViewById(R.id.voiceButton)
        menuButton = findViewById(R.id.menuButton)
        chatView = findViewById(R.id.chatView)
        pythonCodeView = findViewById(R.id.pythonCodeView)
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            animateButton(it as Button)
            val message = inputText.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }

        voiceButton.setOnClickListener {
            animateButton(it as Button)
            speak()
        }

        menuButton.setOnClickListener {
            animateButton(it as Button)
            showPythonScripts()
        }
    }

    private fun initTextToSpeech() {
        tts = TextToSpeech(this, this)
    }

    private fun initPython() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("jarvis.InputProcessor")
        inputProcessor = module.callAttr("InputProcessor")
    }

    private fun loadPythonScripts() {
        val scriptsDir = File(filesDir, PYTHON_SCRIPTS_DIR)
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs()
        }

        scriptsDir.listFiles { _, name -> name.endsWith(".py") }?.forEach { scriptFile ->
            try {
                val scriptContent = scriptFile.readText()
                val scriptName = scriptFile.nameWithoutExtension
                Python.getInstance().getModule("builtins").callAttr("exec", scriptContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendMessage(message: String) {
        chatView.append("You: $message\n")
        inputText.setText("")

        val response = inputProcessor.callAttr("process_input", message)
        val responseStr = response.toString()
        chatView.append("Jarvis: $responseStr\n")
        tts.speak(responseStr, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话...")
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "语音识别不可用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                val spokenText = it[0]
                inputText.setText(spokenText)
                sendMessage(spokenText)
            }
        }
    }

    private fun showPythonScripts() {
        val scriptList = StringBuilder("可用的Python脚本:\n\n")
        val scriptsDir = File(filesDir, PYTHON_SCRIPTS_DIR)
        scriptsDir.listFiles { _, name -> name.endsWith(".py") }?.forEachIndexed { index, file ->
            scriptList.append("${index + 1}. ${file.nameWithoutExtension}\n")
        }
        scriptList.append("\n输入脚本名称来运行脚本。")
        pythonCodeView.text = scriptList.toString()
        pythonCodeView.visibility = View.VISIBLE
    }

    private fun animateButton(button: Button) {
        ObjectAnimator.ofFloat(button, "scaleX", 0.7f).apply {
            duration = 100
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        ObjectAnimator.ofFloat(button, "scaleY", 0.7f).apply {
            duration = 100
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_pressed)
        button.postDelayed({
            button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_normal)
        }, 200)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "语言不支持", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "TTS初始化失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

