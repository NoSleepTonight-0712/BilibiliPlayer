package com.github.zhixinghey0712.bilibiliplayer.util.player;

import com.github.zhixinghey0712.bilibiliplayer.util.SongList;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;

public class PlayListManager {
    private static SongList songList;
    private static SongObject currentSong;

    public static SongList getSongList() {
        return songList;
    }

    public static void setSongList(SongList songList) {
        PlayListManager.songList = songList;
    }

    public static SongObject getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(SongObject currentSong) {
        PlayListManager.currentSong = currentSong;
    }

    public static SongObject nextSong() {
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

    public static SongObject previousSong() {
        int index = songList.getSongObjects().indexOf(currentSong);
        SongObject result;
        if (index == 0) {
            result = songList.getSongObjects().get(songList.getSize() - 1);
        } else {
            result = songList.getSongObjects().get(index - 1);
        }
        currentSong = result;
        return result;
    }
}
