package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongList;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * the main method to getNextSong.
     * <em>notice: the "next" is really the next.</em>
     * It will be called if a song is finished, or the user press the "next song" button.
     * The actual logic is in the following methods:
     *
     * @param mode mode
     * @return song
     * @see #nextSongInDefault()
     * @see #nextSongInRandom()
     * @see #nextSongInSmart()
     * @see #nextSongInLoop()
     */
    @Nullable
    protected static SongObject getNextSong(@NotNull PlayMode mode) {
        if (songList == null) return null;
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

    /**
     * will be called if the user press the "previous song" button.
     * this method will get a history in {@link PlayStack}
     *
     * @return song
     */
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

    @Nullable
    private static SongObject nextSongInRandom() {
        Random random = new Random();
        int r_index = random.nextInt(songList.getSize() - 1);
        return songList.getSongObjects().get(r_index);
    }

    @Deprecated
    private static SongObject nextSongInSmart() {
        // TODO
        return currentSong;
    }

    private static SongObject nextSongInLoop() {
        return currentSong;
    }

    public static void clearFutureStack() {
        history.clearFutureStack();
    }
}
