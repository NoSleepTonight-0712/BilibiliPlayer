package com.github.zhixingheyi0712.bilibiliplayer.util;

public class GlobalVariables {
    public static final String TAG = "Bilibili Player";

    public static final String FAVLIST_INDEX_FILE_NAME = "index_favlist.json";
    public static final String USER_INFO_FILE_NAME = "user.json";
    public static final String USER_FACE_FILE_NAME = "face.jpg";
    public static final String SWITCH_PLAY_PAUSE_TO_SERVICE = "switchPlayPause";
    public static final String PLAY_RESOURCE = "song";
    public static final String CHANGE_MUSIC_TO_SERVICE = "change_music";
    public static final String FORWARD_MUSIC_TO_SERVICE = "change_music_forward";
    public static final String BACKWARD_MUSIC_TO_SERVICE = "backward_music_forward";
    public static final String SONG_LIST = "songlist";
    public static final int RETURN_SONG_LIST_INTENT_CODE = 2;

    public static String FavListIndexFileName(String favListId) {
        return "favlist_id_"+favListId+".json";
    }

    public static String MediaFileName(String bvid) {
        return "media_"+bvid+".mp4";
    }
}
