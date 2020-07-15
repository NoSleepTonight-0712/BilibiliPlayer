package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.media.MediaPlayer;
import android.os.Binder;

import androidx.annotation.Nullable;

public class PlayerBinder extends Binder {
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    private MediaPlayer player;

    private boolean isPlaying;

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    private boolean isPrepared = false;

    public static class PlayBinderPointer {
        @Nullable
        public PlayerBinder getBinder() {
            return binder;
        }

        public void setBinder(PlayerBinder binder) {
            this.binder = binder;
        }

        private PlayerBinder binder;
    }
}
