package com.github.zhixinghey0712.bilibiliplayer.util.player;

import android.graphics.Bitmap;

public class Music {
    private String Name;
    private String Singer;
    private int ListenTimes = 0;
    private Bitmap Cover;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String singer) {
        Singer = singer;
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
}
