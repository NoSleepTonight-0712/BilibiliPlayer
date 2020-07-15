package com.github.zhixingheyi0712.bilibiliplayer.util;

import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlist.FavList;

public class FavListObject {
    private String ListName;
    private String fid;

    private int total;

    public FavListObject(FavList json) {
        this.ListName = json.getTitle();
        this.fid = String.valueOf(json.getId());
        this.total = json.getMedia_count();
    }

    public String getListName() {
        return ListName;
    }

    public String getFid() {
        return fid;
    }

    public int getTotal() {
        return total;
    }
}
