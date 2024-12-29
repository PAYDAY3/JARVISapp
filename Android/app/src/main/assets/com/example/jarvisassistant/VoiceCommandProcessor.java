package com.example.jarvisassistant;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceCommandProcessor {

    private Context context;
    private TextToSpeech tts;

    public VoiceCommandProcessor(Context context) {
        this.context = context;
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle language not supported
                }
            } else {
                // Handle TTS initialization failure
            }
        });
    }

    public void processCommand(String command) {
        command = command.toLowerCase();

        if (command.contains("add task")) {
            addTask(command);
        } else if (command.contains("show tasks")) {
            showTasks();
        } else if (command.contains("set reminder")) {
            setReminder(command);
        } else {
            speakResponse("Sorry, I didn't understand that command.");
        }
    }

    private void addTask(String command) {
        // Extract task details from command and add task
        speakResponse("Task added successfully.");
    }

    private void showTasks() {
        // Retrieve and display tasks
        speakResponse("Here are your tasks.");
        // You might want to open the task list activity/fragment here
    }

    private void setReminder(String command) {
        // Extract reminder details from command and set reminder
        speakResponse("Reminder set successfully.");
    }

    private void speakResponse(String response) {
        tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}

