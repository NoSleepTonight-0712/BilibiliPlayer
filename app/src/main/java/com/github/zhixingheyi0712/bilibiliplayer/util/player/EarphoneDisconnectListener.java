package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.github.zhixingheyi0712.bilibiliplayer.util.json.downloadLink.Audio;

import org.greenrobot.eventbus.EventBus;

public class EarphoneDisconnectListener extends BroadcastReceiver {
    public static boolean enabled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        enabled = true;
        String action = intent.getAction();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            PlayerService.getExoPlayer().setPlayWhenReady(false);
        }
    }
}
