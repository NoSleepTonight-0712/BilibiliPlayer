package com.github.zhixinghey0712.bilibiliplayer.util;

import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.Medias;

public class SongObject {
    private String name, singer;

    public SongObject(Medias medias) {
        this.name = medias.getTitle();
        this.singer = medias.getUpper().getName();
    }

    public String getName() {
        return name;
    }

    public String getSinger() {
        return singer;
    }

}
