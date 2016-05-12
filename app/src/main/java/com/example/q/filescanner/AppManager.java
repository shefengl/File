package com.example.q.filescanner;

import android.app.Application;

public class AppManager extends Application {
    private static AppPreferences sAppPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        // Load Shared Preference
        sAppPreferences = new AppPreferences(this);

    }

    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }

}
