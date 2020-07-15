package com.github.zhixinghey0712.bilibiliplayer.util.info;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.BilibiliAPI;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.json.cid.CidJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.downloadLink.DownloadLinksJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Network {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Handler uiHandler = new Handler();

    public static void getFavListFile(String uid) {
        String API = BilibiliAPI.getFavListAPI(uid);
        Request request = new Request.Builder().url(API).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "get Fav List start");
        try {
            Response response = call.execute();
            Log.i(GlobalVariables.TAG, "get Fav list success.");
            FileOutputStream fileOutputStream =
                    ApplicationMain.getContext()
                            .openFileOutput(GlobalVariables.FAVLIST_INDEX_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(Objects.requireNonNull(response.body()).bytes());
            fileOutputStream.close();
            uiHandler.post(() ->
                    Toast.makeText(ApplicationMain.getContext(),
                            R.string.t_get_favlist_success, Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getUserInfoFile(String uid) {
        String API = BilibiliAPI.getUserInfoAPI(uid);
        Request request = new Request.Builder().url(API).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "get UserInfo start");
        try {
            Response response = call.execute();
            Log.i(GlobalVariables.TAG, "get UserInfo success.");
            FileOutputStream fileOutputStream =
                    ApplicationMain.getContext()
                            .openFileOutput(GlobalVariables.USER_INFO_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(Objects.requireNonNull(response.body()).bytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getUserFace(String link) {
        link = link.replace("http://", "https://");
        Request request = new Request.Builder().url(link).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "get UserFace start");
        try {
            Response response = call.execute();
            Log.i(GlobalVariables.TAG, "get UserFace success.");
            FileOutputStream fileOutputStream =
                    ApplicationMain.getContext()
                            .openFileOutput(GlobalVariables.USER_FACE_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(Objects.requireNonNull(response.body()).bytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getFavListContent(String fid, int total) {
        Log.i(GlobalVariables.TAG, "start fav content page get.");
        ArrayList<String> apis = BilibiliAPI.getFavListContentAPI(fid, total);
        Request request;
        Call call;
        int page = 1;
        FavlistContentJsonBean result = null;
        FavlistContentJsonBean tmp = null;
        for (String api : apis) {
            request = new Request.Builder().url(api).build();
            call = client.newCall(request);
            Log.i(GlobalVariables.TAG, "Get Fav content Page : " + page);
            if (result == null) {
                // 第一段
                try {
                    result = JSON.parseObject(Objects.requireNonNull(call.execute().body()).bytes(),
                            FavlistContentJsonBean.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    tmp = JSON.parseObject(Objects.requireNonNull(call.execute().body()).bytes(), FavlistContentJsonBean.class);
                    result.getData().getMedias().addAll(tmp.getData().getMedias());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            page++;
        }

        try {
            FileOutputStream out = ApplicationMain.getContext().openFileOutput(GlobalVariables.FavListIndexFileName(fid), Context.MODE_PRIVATE);
            out.write(JSON.toJSONString(result).getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取cid，cid数据不会持久化存储
     *
     * @param bvid bv号
     * @return cid
     */
    @Nullable
    public static String getCid(String bvid) {
        return getCid(bvid, 0);
    }

    /**
     * @param bvid bv
     * @param p    分p，从0开始
     * @return cid
     */
    @Nullable
    public static String getCid(String bvid, int p) {
        String api = BilibiliAPI.getCidAPI(bvid);
        Request request = new Request.Builder().url(api).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "get Cid of " + bvid);
        try {
            Response response = call.execute();
            CidJsonBean json = JSON.parseObject
                    (Objects.requireNonNull(response.body()).bytes(), CidJsonBean.class);
            return String.valueOf(json.getData().get(p).getCid());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param cid  cid
     * @param bvid bv
     * @return 下载链接
     */
    @Nullable
    public static String getDownloadLink(String cid, String bvid) {
        String api = BilibiliAPI.getDownloadLinkAPI(cid, bvid);
        Request request = new Request.Builder().url(api).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "get download link of " + bvid);
        try {
            Response response;
            ResponseBody body;
            DownloadLinksJsonBean json;
            int retryTimes = 0;
            while (retryTimes < 3) {
                response = call.execute();
                body = response.body();
                if (body == null) {
                    Log.e(GlobalVariables.TAG, "Get download link of " + bvid + "error, retry");
                    retryTimes++;
                    continue;
                }
                json = JSON.parseObject
                        (Objects.requireNonNull(response.body()).bytes(), DownloadLinksJsonBean.class);
                return String.valueOf(json.getData().getDash().getAudio().get(0).getBaseUrl());
            }
            Log.e(GlobalVariables.TAG, "Too many reties on get download link of "+ bvid);
            return null;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void downloadMedia(@NonNull String bvid, @NonNull String link) {

        Request request = new Request.Builder().url(link)
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3970.5 Safari/537.36")
                .addHeader("Referer", "https://www.bilibili.com/".concat(bvid)).build();
        Call call = client.newCall(request);
        Log.i(GlobalVariables.TAG, "downloading " + bvid + "in link");
        try {
            Response response;
            FileOutputStream out;
            byte[] downloadContent;

            while (true) {
                response = call.execute();
                out = ApplicationMain.getContext()
                        .openFileOutput(GlobalVariables.MediaFileName(bvid), Context.MODE_PRIVATE);
                downloadContent = response.body().bytes();
                if (downloadContent.length < 1024 * 10) { // 10kb以下认为下载出错了
                    Log.e(GlobalVariables.TAG, bvid + "下载可能出错，重新下载");
                } else {
                    // 认为下载成功
                    Log.i(GlobalVariables.TAG, bvid + "下载成功");
                    break;
                }
            }
            out.write(downloadContent);
            out.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
