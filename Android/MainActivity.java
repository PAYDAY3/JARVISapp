package com.example.jarvisassistant;

import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import com.example.jarvisassistant.backend.JarvisBackend;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Button sendButton;
    private Button voiceButton;
    private Button menuButton;
    private TextView chatView;
    private TextView pythonCodeView;
    private TextToSpeech tts;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final String PYTHON_SCRIPTS_DIR = "python_scripts";
    private PyObject jarvisBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        voiceButton = findViewById(R.id.voiceButton);
        menuButton = findViewById(R.id.menuButton);
        chatView = findViewById(R.id.chatView);
        pythonCodeView = findViewById(R.id.pythonCodeView);

        sendButton.setOnClickListener(v -> {
            String message = inputText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });

        voiceButton.setOnClickListener(v -> speak());

        menuButton.setOnClickListener(v -> showPythonScripts());

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 语言数据丢失或不支持
                }
            } else {
                // TTS初始化失败
            }
        });

        // 加载Python脚本
        loadPythonScripts();
        initPython();
        displayConversationHistory();
    }

    private void sendMessage(String message) {
        chatView.append("You: " + message + "\n");
        inputText.setText("");

        PyObject response = jarvisBoard.callAttr("process_command", message);
        String responseStr = response.toString();
        chatView.append("Jarvis: " + responseStr + "\n");
        tts.speak(responseStr, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话...");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            // 处理错误
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = result.get(0);
                inputText.setText(spokenText);
                sendMessage(spokenText);
            }
        }
    }

    private void showPythonScripts() {
        StringBuilder scriptList = new StringBuilder("可用的Python脚本:\n\n");
        File scriptsDir = new File(getFilesDir(), PYTHON_SCRIPTS_DIR);
        File[] scriptFiles = scriptsDir.listFiles((dir, name) -> name.endsWith(".py"));

        if (scriptFiles != null) {
            for (int i = 0; i < scriptFiles.length; i++) {
                String scriptName = scriptFiles[i].getName().replace(".py", "");
                scriptList.append(i + 1).append(". ").append(scriptName).append("\n");
            }
        }

        scriptList.append("\n输入脚本名称来运行脚本。");
        pythonCodeView.setText(scriptList.toString());
        pythonCodeView.setVisibility(View.VISIBLE);
    }

    private void loadPythonScripts() {
        File scriptsDir = new File(getFilesDir(), PYTHON_SCRIPTS_DIR);
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs();
        }

        File[] scriptFiles = scriptsDir.listFiles((dir, name) -> name.endsWith(".py"));
        if (scriptFiles != null) {
            for (File scriptFile : scriptFiles) {
                try {
                    String scriptContent = readFile(scriptFile);
                    String scriptName = scriptFile.getName().replace(".py", "");
                    JarvisBackend.addPythonScript(scriptName, scriptContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readFile(File file) throws IOException {
        byte[] encoded = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(encoded);
        }
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private void initPython() {
        PythonInterpreter interpreter = new PythonInterpreter();
        PyObject jarvisModule = interpreter.exec("import jarvis_module; jarvis_module"); // Assuming jarvis_module.py exists
        jarvisBoard = jarvisModule.callAttr("JarvisInteractionBoard");
    }


    private void displayConversationHistory() {
        PyObject history = jarvisBoard.callAttr("get_conversation_history");
        chatView.setText("");
        for (PyObject item : history.asList()) {
            String sender = item.asList().get(0).toString();
            String message = item.asList().get(1).toString();
            chatView.append(sender + ": " + message + "\n");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
