package com.turndapage.wear.watchface.watchfacedarko;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsUtil {
    private static String CURRENT_BACKGROUND = "current_background";
    private static String CURRENT_LAYOUT = "current_layout";
    private static String CURRENT_STYLE = "current_style";
    private static String PRO_UNLOCKED = "pro_unlocked";
    private static String CURRENT_WALLPAPER = "current_wallpaper";
    private static String DARK_THEME = "dark_theme";
    private static String HIDE_CLOCK = "hide_clock";
    private static String HIDE_COMPLICATIONS = "hide_complications";

    public static void SetHideComplications(Context context, boolean hide) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(HIDE_COMPLICATIONS, hide).apply();
    }

    public static boolean GetHideComplications(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(HIDE_COMPLICATIONS, true);
    }

    public static void SetCurrentBackground(Context context, int background) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(CURRENT_BACKGROUND, background).apply();
    }

    public static int GetCurrentBackground(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(CURRENT_BACKGROUND, R.drawable.half_metal);
    }

    public static void SetCurrentLayout(Context context, int layout) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(CURRENT_LAYOUT, layout).apply();
    }

    public static int GetCurrentLayout(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(CURRENT_LAYOUT, 0);
    }

    public static void SetCurrentStyle(Context context, int style) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(CURRENT_STYLE, style).apply();
    }

    public static int GetCurrentStyle(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(CURRENT_STYLE, 2);
    }

    public static void SetProUnlocked(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(PRO_UNLOCKED, true).apply();
    }

    public static boolean GetProUnlocked(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(PRO_UNLOCKED, false);
    }

    public static void SetCurrentWallpaper(Context context, int i) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(CURRENT_WALLPAPER, i).apply();
    }

    public static int GetCurrentWallpaper(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(CURRENT_WALLPAPER, 0);
    }

    public static void SetDarkTheme(Context context, boolean dark) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(DARK_THEME, dark).apply();
    }

    public static boolean GetDarkTheme(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(DARK_THEME, false);
    }

    public static void SetHideClock(Context context, boolean hideClock) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(HIDE_CLOCK, hideClock).apply();
    }

    public static boolean GetHideClock(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(HIDE_CLOCK, false);
    }
}
