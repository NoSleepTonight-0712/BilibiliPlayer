package com.github.zhixinghey0712.bilibiliplayer.util.player;

import android.os.Handler;

import com.github.zhixinghey0712.bilibiliplayer.util.PlayMode;
import com.github.zhixinghey0712.bilibiliplayer.util.SongList;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.UserSettings;

import java.util.Random;

public class PlayListManager {
    private static SongList songList;
    private static SongObject currentSong;

    private static PlayListHistory history = new PlayListHistory();

    public static void setSongList(SongList songList) {
        PlayListManager.songList = songList;
    }

    public static void setCurrentSong(SongObject currentSong) {
        PlayListManager.currentSong = currentSong;
    }

    public static SongObject getCurrentSong() {
        return currentSong;
    }

    public static SongObject nextPlay(boolean isPrevious) {
        SongObject result;
        if (isPrevious) {
            result = history.back(currentSong);
        } else {
            result = history.finish(currentSong);
        }
        if (result != null) currentSong = result;
        return result;
    }

    protected static SongObject getNextSong(PlayMode mode) {
        SongObject result;
        switch (mode) {
            case RANDOM:
                result = nextSongInRandom();
                break;
            case SMART:
                result = nextSongInSmart();
                break;
            case LOOP:
                result = nextSongInLoop();
                break;
            default:
                result = nextSongInDefault();
                break;
        }
        return result;
    }

    private static SongObject nextSongInDefault() {
        int index = songList.getSongObjects().indexOf(currentSong);
        SongObject result;
        if (index == songList.getSize() - 1) {
            result = songList.getSongObjects().get(0);
        } else {
            result = songList.getSongObjects().get(index + 1);
        }
        currentSong = result;
        return result;
    }

    private static SongObject nextSongInRandom() {
        Random random = new Random();
        int r_index = random.nextInt(songList.getSize() - 1);
        return songList.getSongObjects().get(r_index);
    }

    private static SongObject nextSongInSmart() {
        // TODO
        return currentSong;
    }

    private static SongObject nextSongInLoop() {
        return currentSong;
    }
}
