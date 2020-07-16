package com.github.zhixingheyi0712.bilibiliplayer.util;

public class GlobalVariables {
    public static final String TAG = "Bilibili Player";

    public static final String FAVLIST_INDEX_FILE_NAME = "index_favlist.json";
    public static final String USER_INFO_FILE_NAME = "user.json";
    public static final String USER_FACE_FILE_NAME = "face.jpg";
    public static final String STOP_PLAYING = "stop_playing";
    public static final int RETURN_SONG_LIST_INTENT_CODE = 2;

    public static String FavListIndexFileName(String favListId) {
        return "favlist_id_"+favListId+".json";
    }

    public static String MediaFileName(String bvid) {
        return "media_"+bvid+".mp4";
    }
}
