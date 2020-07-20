package com.github.zhixingheyi0712.bilibiliplayer.util;

import android.graphics.Bitmap;

import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent.Medias;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SongObject implements Serializable {
    private String name;
    private String singer;

    public String getBvid() {
        return bvid;
    }

    private String bvid;
    private int ListenTimes = 0;
    private Bitmap Cover;
    private String CoverImageLink;

    public int getIndexInList() {
        return indexInList;
    }

    public void setIndexInList(int indexInList) {
        this.indexInList = indexInList;
    }

    private int indexInList = -1;

    public int getP() {
        return p;
    }

    private int p = 0;  // 暂时先不写分p

    public SongObject(Medias medias, int indexInList) {
        this.name = medias.getTitle();
        this.singer = medias.getUpper().getName();
        this.bvid = medias.getBvid();
        this.CoverImageLink = medias.getCover();
        this.indexInList = indexInList;
    }

    public String getName() {
        return name;
    }

    public String getSinger() {
        return singer;
    }

    public int getListenTimes() {
        return ListenTimes;
    }

    public void setListenTimes(int listenTimes) {
        ListenTimes = listenTimes;
    }

    public Bitmap getCover() {
        return Cover;
    }

    public void setCover(Bitmap cover) {
        Cover = cover;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("Song: id: %s, name: %s", bvid, name);
    }

    public String getInfoString() {
        return name + " - " + singer;
    }
}
