package com.github.zhixingheyi0712.bilibiliplayer.util.json.favlist;

public class FavList {

    private long id;
    private long fid;
    private long mid;
    private int attr;
    private String title;
    private int fav_state;
    private int media_count;
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }
    public long getFid() {
        return fid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }
    public long getMid() {
        return mid;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }
    public int getAttr() {
        return attr;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setFav_state(int fav_state) {
        this.fav_state = fav_state;
    }
    public int getFav_state() {
        return fav_state;
    }

    public void setMedia_count(int media_count) {
        this.media_count = media_count;
    }
    public int getMedia_count() {
        return media_count;
    }

}