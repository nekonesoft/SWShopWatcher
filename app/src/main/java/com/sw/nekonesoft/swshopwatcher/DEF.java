package com.sw.nekonesoft.swshopwatcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kishimoto on 2016/11/25.
 */

public class DEF {

    // デバッグログ用タグ
    public static final String TAG = "SWShopWatcher";

    // SettingsActivityのリクエストコード
    public static final int REQUEST_SETTING = 101;

    // サマナーズウォー起動用のパッケージ名
    public static final String PACKAGE_NAME_SW
            = "com.com2us.smon.normal.freefull.google.kr.android.common";

    // ユニークなIDを取得するために、R.layout.activity_fileselectのリソースIDを使う
    public static final int NOTIFICATION_ID = R.layout.activity_main;

    // Preferencesアクセス用マクロ
    static public String loadPreferenceString(Context context, String key, String def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, def);
    }
    static public int loadPreferenceInt(Context context, String key, int def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, def);
    }
    static public long loadPreferenceLong(Context context, String key, long def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, def);
    }
    static public float loadPreferenceFloat(Context context, String key, float def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(key, def);
    }
    static public boolean loadPreferenceBoolean(Context context, String key, boolean def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, def);
    }

    static public void savePreferenceString(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.commit();
    }
    static public void savePreferenceInt(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.commit();
    }
    static public void savePreferenceLong(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(key, value);
        ed.commit();
    }
    static public void savePreferenceFloat(Context context, String key, float value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putFloat(key, value);
        ed.commit();
    }
    static public void savePreferenceBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.commit();
    }
    static public void removePreferenceKey(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(key);
        ed.commit();
    }
}
