package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.media.audiofx.AcousticEchoCanceler;

import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;

public class PlayerEvents {
    /**
     * Update the button in
     * {@link com.github.zhixingheyi0712.bilibiliplayer.ui.PlayerFragment}
     * notice it cannot actually change the player state.
     */
    @Deprecated
    public static class SetPlayingButtonState {
        public boolean isPlaying() {
            return playing;
        }

        private boolean playing;

        public SetPlayingButtonState(boolean playing) {
            this.playing = playing;
        }
    }

    /**
     * switch play/pause to player.
     * notice it cannot change the button.
     */
    @Deprecated
    public static class SetPlayingServiceState {
        private boolean force_pause = false;

        public void enableForcePause() {
            force_pause = true;
        }

        public boolean isForcePauseEnabled() {
            return force_pause;
        }
    }

    /**
     * set player resource.
     */
    @Deprecated
    public static class SetPlayerResource {
        public SongObject getSong() {
            return song;
        }

        public SetPlayerResource(SongObject song) {
            this.song = song;
        }

        private SongObject song;
    }

    /**
     * notice the "next" means the next song given to the player.
     * It maybe the previous one is previous == true.
     */
    public static class PlayNextSong {
        public boolean isPrevious() {
            return previous;
        }

        private boolean previous;

        public PlayNextSong(boolean previous) {
            this.previous = previous;
        }
    }

    /**
     * update Info TextView in
     * {@link com.github.zhixingheyi0712.bilibiliplayer.ui.PlayerFragment}
     */
    public static class SetPlayingInfo {
        private SongObject song;

        public SetPlayingInfo(SongObject song) {
            this.song = song;
        }

        public SongObject getSong() {
            return song;
        }
    }

    /**
     * send a toast in {@link com.github.zhixingheyi0712.bilibiliplayer.MainActivity}
     */
    public static class HintToast {
        private int id;
        public HintToast(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * update play/pause button
     * always be sent by ui.
     */
    @Deprecated
    public static class ResendUpdatePlayPauseButton {}
}
