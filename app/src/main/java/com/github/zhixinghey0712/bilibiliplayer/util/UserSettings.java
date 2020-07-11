package com.github.zhixinghey0712.bilibiliplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;

public class UserSettings {
    private static SharedPreferences pref =
            ApplicationMain.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor =
            pref.edit();

    public static String getUid() {
        return pref.getString("uid", "000");
    }

    public static void setUid(String uid) {
        editor.putString("uid", uid);
        editor.apply();
    }

    public static PlayMode getPlayMode() {
        String trigger = pref.getString("play_mode", "default");
        switch (trigger) {
            case "default":
                return PlayMode.DEFAULT;
            case "random":
                return PlayMode.RANDOM;
            case "smart":
                return PlayMode.SMART;
            default:
                return PlayMode.DEFAULT;
        }
    }

    public static void setPlayMode(PlayMode mode) {
        String key = "play_mode";
        switch (mode) {
            case SMART:
                editor.putString(key, "smart");
                break;
            case RANDOM:
                editor.putString(key, "random");
                break;
            case DEFAULT:
                editor.putString(key, "default");
                break;
            default:
                editor.putString(key, "default");
                break;
        }
        editor.apply();
    }
}
