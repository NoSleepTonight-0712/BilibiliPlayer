package com.github.zhixinghey0712.bilibiliplayer.util.info;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlist.FavListJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.user.UserInfoJsonBean;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class LocalInfoManager {

    /**
     * 获取个人收藏夹
     *
     * @param uid  uid
     * @param mode 更新模式
     * @return json的实体类
     */
    public static FavListJsonBean getFavList(String uid, UpdateMode mode) {
        if (mode == UpdateMode.ONLINE || !isFileExists(GlobalVariables.FAVLIST_INDEX_FILE_NAME)) {
            Log.i(GlobalVariables.TAG, "联网获取FavList");
            Network.getFavListFile(uid);
        }

        FavListJsonBean json = readJson(GlobalVariables.FAVLIST_INDEX_FILE_NAME, FavListJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "出现错误");
        }
        return json;
    }

    public static UserInfoJsonBean getUserInfo(String uid, UpdateMode mode) {
        if (mode == UpdateMode.ONLINE || !isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) {
            Log.i(GlobalVariables.TAG, "联网获取UserInfo");
            Network.getUserInfoFile(uid);
        }
        UserInfoJsonBean json = readJson(GlobalVariables.USER_INFO_FILE_NAME, UserInfoJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "出现错误");
        }
        return json;
    }

    public static UserInfoJsonBean getUserInfo() {
        return getUserInfo("-1", UpdateMode.LOCAL);
    }

    public static Bitmap saveUserFace(String link, UpdateMode mode) {
        if (mode == UpdateMode.ONLINE || !isFileExists(GlobalVariables.USER_FACE_FILE_NAME)) {
            Log.i(GlobalVariables.TAG, "联网获取UserFace");
            Network.getUserFace(link);
        }
        try {
            return BitmapFactory.decodeStream
                    (ApplicationMain.getContext().openFileInput(GlobalVariables.USER_FACE_FILE_NAME));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FavlistContentJsonBean getFavListContent(String fid, int total, UpdateMode mode) {
        String fileName = GlobalVariables.FavListIndexFileName(fid);
        if (mode == UpdateMode.ONLINE || !isFileExists(fileName)) {
            Log.i(GlobalVariables.TAG, "联网获取收藏夹内容");
            Network.getFavListContent(fid, total);
        }
        FavlistContentJsonBean json = readJson(fileName, FavlistContentJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "获取收藏夹失败");
        }
        return json;
    }

    @Nullable
    public static File getMediaFile(SongObject songObject, UpdateMode mode) {
        return getMediaFile(songObject.getBvid(), songObject.getP(), mode);
    }

    @Nullable
    public static File getMediaFile(String bvid, int p, UpdateMode mode) {
        String fileName = GlobalVariables.MediaFileName(bvid);
        if (mode != UpdateMode.FORCE_LOCAL) {
            if (mode == UpdateMode.ONLINE || getFileState(fileName) != FileState.EXIST) {
                Log.i(GlobalVariables.TAG, "downloading..." + bvid);
                String cid = Network.getCid(bvid, p);
                if (cid == null) return null;
                String link = Network.getDownloadLink(cid, bvid);
                if (link == null) return null;
                Network.downloadMedia(bvid, link);
            }
        }
        try {
//            createMediaFolder();
            File media = new File(ApplicationMain.getContext().getFilesDir()
                    .getCanonicalPath().concat("/" + GlobalVariables.MediaFileName(bvid)));
            if (media.exists()) {
                return media;
            } else {
                Toast.makeText(ApplicationMain.getContext(), R.string.t_download_first, Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static <T> T readJson(String fileName, Class<T> clazz) {
        try {
            FileInputStream in = ApplicationMain.getContext().openFileInput(fileName);
            String line = null;
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (true) {
                line = reader.readLine();
                if (line == null) break;
                buffer.append(line).append("\n");
            }
            in.close();
            reader.close();

            return JSON.parseObject(buffer.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileState getFileState(String fileName) {
        try {
            File f = new File(ApplicationMain.getContext().getFilesDir().getCanonicalPath().concat("/").concat(fileName));
            if (f.exists()) {
                if (f.length() < 5) {
                    return FileState.SEEMS_BROKEN;
                }
                return FileState.EXIST;
            } else {
                return FileState.NOT_EXIST;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return FileState.NOT_EXIST;
        }
    }

    public static boolean isFileExists(String fileName) {
        try {
            return new File(ApplicationMain.getContext().getFilesDir().getCanonicalPath().concat("/").concat(fileName)).exists();
        } catch (IOException e) {
            return false;
        }
    }

    private static void createMediaFolder() {
        try {
            File f = new File(ApplicationMain.getContext().getFilesDir().getCanonicalPath().concat("/media"));
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum FileState {
        EXIST,
        NOT_EXIST,
        SEEMS_BROKEN
    }
}
