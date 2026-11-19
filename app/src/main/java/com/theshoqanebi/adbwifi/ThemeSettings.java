package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * The user's theme choice: follow the system, or always light or dark.
 * Stored next to the port in the "Settings" preferences.
 */
public final class ThemeSettings {
    public static final int SYSTEM = 0;
    public static final int LIGHT = 1;
    public static final int DARK = 2;

    private static final String PREFERENCES = "Settings";
    private static final String KEY = "theme";

    /** Labels in the same order as the SYSTEM, LIGHT and DARK values. */
    public static final int[] LABELS = {R.string.theme_system, R.string.theme_light, R.string.theme_dark};

    private ThemeSettings() {
    }

    public static int get(Context context) {
        return preferences(context).getInt(KEY, SYSTEM);
    }

    /** Saves the choice and applies it; open activities are recreated in the new theme. */
    public static void set(Context context, int theme) {
        preferences(context).edit().putInt(KEY, theme).apply();
        apply(theme);
    }

    /** Applies the saved choice. Call before any activity is created. */
    public static void applySaved(Context context) {
        apply(get(context));
    }

    private static void apply(int theme) {
        int mode;
        if (theme == LIGHT) {
            mode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (theme == DARK) {
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
