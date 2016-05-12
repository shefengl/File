package com.example.q.filescanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String KeyCustomFilename = "prefCustomFilename";
    public static final String KeySortMode = "prefSortMode";
    public static final String KeyCustomPath = "prefCustomPath";

    public AppPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.context = context;
    }

    public String getCustomFilename() {
        return sharedPreferences.getString(KeyCustomFilename, "1");
    }
    public void setCustomFilename(String res) {
        editor.putString(KeyCustomFilename, res);
        editor.commit();
    }

    public String getSortMode() {
        return sharedPreferences.getString(KeySortMode, "1");
    }
    public void setSortMode(String res) {
        editor.putString(KeySortMode, res);
        editor.commit();
    }

    public String getCustomPath() {
        return sharedPreferences.getString(KeyCustomPath, UtilsApp.getDefaultAppFolder().getPath());
    }

    public void setCustomPath(String path) {
        editor.putString(KeyCustomPath, path);
        editor.commit();
    }

}
