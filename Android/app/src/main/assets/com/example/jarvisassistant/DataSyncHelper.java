package com.example.jarvisassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataSyncHelper {

    private static final String PREFS_NAME = "JarvisAssistantPrefs";
    private static final String TASKS_KEY = "tasks";
    private static final String REMINDERS_KEY = "reminders";

    public static void saveTasks(Context context, List<Task> tasks) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    public static List<Task> loadTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(TASKS_KEY, null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveReminders(Context context, List<Reminder> reminders) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminders);
        editor.putString(REMINDERS_KEY, json);
        editor.apply();
    }

    public static List<Reminder> loadReminders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(REMINDERS_KEY, null);
        Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void backupData(Context context, String fileName) {
        try {
            File backupFile = new File(context.getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(backupFile);
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().commit();
            File sharedPrefsFile = new File(context.getFilesDir().getParent() + "/shared_prefs/" + PREFS_NAME + ".xml");
            FileInputStream fis = new FileInputStream(sharedPrefsFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            fis.close();
            Log.d("DataSyncHelper", "Backup created successfully");
        } catch (IOException e) {
            Log.e("DataSyncHelper", "Error creating backup", e);
        }
    }

    public static void restoreData(Context context, String fileName) {
        try {
            File backupFile = new File(context.getExternalFilesDir(null), fileName);
            FileInputStream fis = new FileInputStream(backupFile);
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().clear().commit();
            File sharedPrefsFile = new File(context.getFilesDir().getParent() + "/shared_prefs/" + PREFS_NAME + ".xml");
            FileOutputStream fos = new FileOutputStream(sharedPrefsFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            fis.close();
            Log.d("DataSyncHelper", "Backup restored successfully");
        } catch (IOException e) {
            Log.e("DataSyncHelper", "Error restoring backup", e);
        }
    }
}

