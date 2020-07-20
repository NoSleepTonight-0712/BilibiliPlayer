package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;

public class MediaTag {
    public SongObject getSongObject() {
        return songObject;
    }

    private SongObject songObject;

    public MediaTag(SongObject song) {
        songObject = song;
    }

    public String getInfoString() {
        return songObject.getInfoString();
    }

    public int getIndexInSongList() {
        return songObject.getIndexInList();
    }
}
