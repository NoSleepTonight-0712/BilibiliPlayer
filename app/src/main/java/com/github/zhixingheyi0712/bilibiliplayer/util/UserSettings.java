package com.github.zhixingheyi0712.bilibiliplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.zhixingheyi0712.bilibiliplayer.ApplicationMain;

import org.jetbrains.annotations.NotNull;

public class UserSettings {
    private static SharedPreferences pref =
            ApplicationMain.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor =
            pref.edit();

    public static String getUid() {
        return pref.getString("uid", "-1");
    }

    public static void setUid(String uid) {
        editor.putString("uid", uid);
        editor.apply();
    }

    public static PlayMode getPlayMode() {
        String trigger = pref.getString("play_mode", "default");
        switch (trigger) {
            case "loop":
                return PlayMode.LOOP;
            case "random":
                return PlayMode.RANDOM;
            case "smart":
                return PlayMode.SMART;
            default:
                return PlayMode.DEFAULT;
        }
    }

    public static void setPlayMode(@NotNull PlayMode mode) {
        String key = "play_mode";
        switch (mode) {
            case SMART:
                editor.putString(key, "smart");
                break;
            case RANDOM:
                editor.putString(key, "random");
                break;
            case LOOP:
                editor.putString(key, "loop");
                break;
            default:
                editor.putString(key, "default");
                break;
        }
        editor.apply();
    }

    public static void setName(String name) {
        editor.putString("name", name);
        editor.apply();
    }

    public static String getName() {
        return pref.getString("name", "");
    }

    public static boolean isLogin() {
        return !getUid().equals("-1");
    }
}
