package com.example.dingtu2.myapplication.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
    public static String PREFERENCE_NAME = "AndroidTTS";

    private SharedPreferencesUtils() {
        throw new AssertionError();
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        Editor var4 = var3.edit();
        var4.putString(key, value);
        return var4.commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, (String)null);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return var3.getString(key, defaultValue);
    }

    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        Editor var4 = var3.edit();
        var4.putInt(key, value);
        return var4.commit();
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return var3.getInt(key, defaultValue);
    }

    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences var4 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        Editor var5 = var4.edit();
        var5.putLong(key, value);
        return var5.commit();
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, -1L);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences var4 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return var4.getLong(key, defaultValue);
    }

    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        Editor var4 = var3.edit();
        var4.putFloat(key, value);
        return var4.commit();
    }

    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1.0F);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return var3.getFloat(key, defaultValue);
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        Editor var4 = var3.edit();
        var4.putBoolean(key, value);
        return var4.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences var3 = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return var3.getBoolean(key, defaultValue);
    }
}

