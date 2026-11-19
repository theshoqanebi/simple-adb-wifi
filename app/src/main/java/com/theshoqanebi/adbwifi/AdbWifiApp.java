package com.theshoqanebi.adbwifi;

import android.app.Application;

public class AdbWifiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Apply the saved theme before the first activity draws, so it never flashes the wrong one.
        ThemeSettings.applySaved(this);
    }
}
