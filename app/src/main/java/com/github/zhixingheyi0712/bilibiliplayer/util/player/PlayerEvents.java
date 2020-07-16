package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.media.audiofx.AcousticEchoCanceler;

import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;

public class PlayerEvents {
    public static class SetPlayingButtonState {
        public boolean isPlaying() {
            return playing;
        }

        private boolean playing;

        public SetPlayingButtonState(boolean playing) {
            this.playing = playing;
        }
    }

    public static class SetPlayingServiceState {
    }

    public static class SetPlayerResource {
        public SongObject getSong() {
            return song;
        }

        public SetPlayerResource(SongObject song) {
            this.song = song;
        }

        private SongObject song;
    }

    public static class PlayNextSong {
        public boolean isPrevious() {
            return previous;
        }

        private boolean previous;

        public PlayNextSong(boolean previous) {
            this.previous = previous;
        }
    }

    public static class SetPlayingInfo {
        private SongObject song;

        public SetPlayingInfo(SongObject song) {
            this.song = song;
        }

        public SongObject getSong() {
            return song;
        }
    }

    public static class HintToast {
        private int id;
        public HintToast(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
