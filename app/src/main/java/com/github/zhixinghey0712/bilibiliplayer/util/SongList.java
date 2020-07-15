package com.github.zhixinghey0712.bilibiliplayer.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SongList implements Serializable {
    private ArrayList<SongObject> songObjects = new ArrayList<>();
    private String name;
    private String fid;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SongObject> getSongObjects() {
        return songObjects;
    }

    public void setSongObjects(ArrayList<SongObject> songObjects) {
        this.songObjects = songObjects;
    }

    public int getSize() {
        return this.songObjects.size();
    }

    @Override
    public SongList clone() {
        SongList songList = new SongList();
        songList.setFid(this.fid);
        songList.setName(this.name);
        songList.setSongObjects((ArrayList<SongObject>) this.songObjects.clone());
        return songList;
    }
}
