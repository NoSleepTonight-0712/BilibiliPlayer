package com.github.zhixinghey0712.bilibiliplayer.util;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Locale;

public class BilibiliAPI {
    /**
     * @param uid 或者mid(参数中是mid)
     * @return API String
     */
    public static String getFavListAPI(String uid) {
        final String API = "https://api.bilibili.com/x/v3/fav/folder/created/list-all?up_mid=";
        return API.concat(uid);
    }

    /**
     * 个人信息
     * @param uid uid
     * @return API String
     */
    public static String getUserInfoAPI(String uid) {
        final String API = "https://api.bilibili.com/x/space/acc/info?mid=";
        return API.concat(uid);
    }

    /**
     * 收藏夹内容
     * @param fid 收藏夹ID
     * @param total 内容总数
     * @return API的ArrayList
     */
    public static ArrayList<String> getFavListContentAPI(String fid, int total) {
        ArrayList<String> result = new ArrayList<>();
        int totalPages = total / 20 + 1;
        for (int page = 1; page <= totalPages; page++) {
            result.add(String.format(Locale.CHINA,
                    "https://api.bilibili.com/x/v3/fav/resource/list?media_id=%s&pn=%d&ps=20",fid, page));
        }
        return result;
    }

    /**
     * @param bvid bv号
     * @return 获取Cid的API
     */
    public static String getCidAPI(String bvid) {
        final String API = "https://api.bilibili.com/x/player/pagelist?bvid=";
        return API.concat(bvid);
    }

    /**
     * @param cid cid
     * @param bvid bv号
     * @return 下载链接API
     */
    public static String getDownloadLinkAPI(String cid, String bvid) {
        final String API = "https://api.bilibili.com/x/player/playurl?cid=%s&fnver=0&fnval=16&type=&otype=json&bvid=%s";
        return String.format(API, cid, bvid);
    }
}
