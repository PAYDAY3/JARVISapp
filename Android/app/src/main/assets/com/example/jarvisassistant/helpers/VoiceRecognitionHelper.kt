package com.example.jarvisassistant.helpers

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher

class VoiceRecognitionHelper(private val activityResultLauncher: ActivityResultLauncher<Intent>) {

    fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您的指令")
        }
        activityResultLauncher.launch(intent)
    }

    companion object {
        fun processResult(resultCode: Int, data: Intent?): String? {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!result.isNullOrEmpty()) {
                    return result[0]
                }
            }
            return null
        }
    }
}

