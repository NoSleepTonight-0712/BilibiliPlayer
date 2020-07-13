package com.github.zhixinghey0712.bilibiliplayer.util;

public class FavListObject {
    private String ListName;
    private String fid;

    public FavListObject(com.github.zhixinghey0712.bilibiliplayer.util.json.favlist.FavList json) {
        this.ListName = json.getTitle();
        this.fid = String.valueOf(json.getFid());
    }

    public FavListObject(String name, String fid) {
        ListName = name;
        this.fid = fid;
    }

    public String getListName() {
        return ListName;
    }

    public String getFid() {
        return fid;
    }
}
