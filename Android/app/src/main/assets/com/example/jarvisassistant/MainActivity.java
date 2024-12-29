package com.example.jarvisassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, GestureHelper.OnGestureListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fab;
    private SearchView searchView;
    private ImageButton voiceInputButton;
    private TextToSpeech tts;
    private GestureHelper gestureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.searchView);
        voiceInputButton = findViewById(R.id.voiceInputButton);

        setupViewPager();
        setupBottomNavigation();
        setupFab();
        setupSearchView();
        setupVoiceInput();
        initTTS();

        gestureHelper = new GestureHelper(this, this);
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(gestureHelper);

        checkAndRequestPermissions();

        NotificationHelper.createNotificationChannel(this);
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "语音识别权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "语音识别权限被拒绝，某些功能可能无法使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupVoiceInput() {
        voiceInputButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition();
            } else {
                Toast.makeText(this, "请先授予语音识别权限", Toast.LENGTH_SHORT).show();
                checkAndRequestPermissions();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您的指令");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "您的设备不支持语音输入", Toast.LENGTH_SHORT).show();
        }
    }

    // ... 其他现有方法 ...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                processVoiceInput(spokenText);
            }
        }
    }

    private void processVoiceInput(String input) {
        // 处理语音输入
        String response = "您说的是：" + input;
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        speakOut(response);
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // ... 其他现有方法 ...

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "不支持中文TTS", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TTS初始化失败", Toast.LENGTH_SHORT).show();
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

    // ... 其他现有方法（如setupViewPager, setupBottomNavigation等） ...
}

