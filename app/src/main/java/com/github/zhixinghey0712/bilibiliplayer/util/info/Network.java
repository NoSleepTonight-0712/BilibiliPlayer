package com.github.zhixinghey0712.bilibiliplayer.util.info;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.BilibiliAPI;
import com.github.zhixinghey0712.bilibiliplayer.util.CallbackFunction;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import android.os.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
