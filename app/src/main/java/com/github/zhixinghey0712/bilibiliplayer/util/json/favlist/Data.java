package com.github.zhixinghey0712.bilibiliplayer.util.json.favlist;
import java.util.List;

public class Data {

    private int count;
    private List<FavList> list;
    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }

    public void setList(List<FavList> list) {
        this.list = list;
    }
    public List<FavList> getList() {
        return list;
    }

}