package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private TextView loadingTextView, permissionStatusTextView, resultTextView;
    private ImageView imageView;
    private EditText editText;
    private Button buttonTakePicture;
    private DatabaseHelper dbHelper;
    private Bitmap imageBitmap;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingTextView = findViewById(R.id.loading_text_view);
        permissionStatusTextView = findViewById(R.id.permission_status_text_view);
        resultTextView = findViewById(R.id.text_view);
        imageView = findViewById(R.id.image_view);
        editText = findViewById(R.id.edit_text);
        buttonTakePicture = findViewById(R.id.button_take_picture);
        dbHelper = new DatabaseHelper(this);

        // 初始化 TextToSpeech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        // 请求权限
        requestPermissions();

        // 设置拍照按钮的点击事件
        buttonTakePicture.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, 
                    new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.VIBRATE
                    }, 
                    REQUEST_PERMISSION_CODE);
        } else {
            initApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                initApp();
            } else {
                permissionStatusTextView.setText("权限被拒绝");
                Toast.makeText(this, "需要所有权限才能运行应用程序", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initApp() {
        try {
            // 使用 Chaquopy 调用 Python
            Python py = Python.getInstance();
            PyObject pyObj = py.getModule("script_name"); // 注意：Python 文件名不包括 .py 扩展名
            PyObject pyResult = pyObj.callAttr("process_image", imageBitmap); // 假设有一个 process_image 函数
            String resultFromPython = pyResult.toJava(String.class);
            resultTextView.setText(resultFromPython);
            speak("执行Python脚本，结果: " + resultFromPython);
            showNotification("Python执行", "结果: " + resultFromPython);

            // 使用从 Python 获得的结果或处理离线数据
            if (!NetworkUtil.isNetworkAvailable(this)) {
                String cachedData = dbHelper.loadData();
                if (cachedData != null) {
                    resultTextView.setText(cachedData);
                } else {
                    resultTextView.setText("网络不可用且没有缓存数据。" + "\nPython 结果: " + resultFromPython);
                }
            } else {
                new DownloadDataTask().execute("http://example.com/data");
                // 同时显示 Python 结果
                resultTextView.setText("从 Python 获取: " + resultFromPython);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultTextView.setText("初始化应用时出错：" + e.getMessage());
            logEvent("Error during initApp: " + e.getMessage());
            speak("初始化应用时出错。");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "general_notifications";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "General Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);

        notificationManager.notify(1, notificationBuilder.build());
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void logEvent(String event) {
        // 简单示例，可以改为记录到文件或数据库
        System.out.println(event);
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "网络请求失败";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("网络请求失败")) {
                dbHelper.saveData(result);
                resultTextView.setText(resultTextView.getText() + "\n下载的数据: " + result);
            } else {
                resultTextView.setText(resultTextView.getText() + "\n下载数据失败。");
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        }
    }
}
