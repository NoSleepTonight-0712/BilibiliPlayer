package com.github.zhixinghey0712.bilibiliplayer.util;

public class BilibiliAPI {
    /**
     *
     * @param uid 或者mid(参数中是mid)
     * @return API String
     */
    public static String getFavListAPI(String uid) {
        final String API = "https://api.bilibili.com/x/v3/fav/folder/created/list-all?up_mid=";
        return API.concat(uid);
    }

    public static String getUserInfoAPI(String uid) {
        final String API = "https://api.bilibili.com/x/space/acc/info?mid=";
        return API.concat(uid);
    }
}
