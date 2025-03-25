package com.eipna.dimediary.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;

public class PreferenceUtil {

    private final SharedPreferences sharedPreferences;

    public PreferenceUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
    }

    public void setAlarm(long timestamp) {
        sharedPreferences.edit().putLong("alarm", timestamp).apply();
    }

    public long getAlarm() {
        return sharedPreferences.getLong("alarm", -1);
    }
}