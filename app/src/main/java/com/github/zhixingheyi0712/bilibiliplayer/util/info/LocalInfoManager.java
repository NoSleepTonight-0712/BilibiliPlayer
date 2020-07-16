package com.github.zhixingheyi0712.bilibiliplayer.util.info;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.github.zhixingheyi0712.bilibiliplayer.ApplicationMain;
import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlist.FavListJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.user.UserInfoJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerEvents;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Manage resource getting.
 * Manage using LOCAL update or ONLINE update.
 * {@link UpdateMode}
 * <p>
 * The relationship of favlist, favlistcontent and media:
 * <p>
 * favlistcontent    media
 * |                 |
 * user - favlist --favlistcontent ---media
 * \                 \
 * favlistcontent    media
 * <p>
 * notice that a user have only one favlist, but may have many favlistcontent, and each favlistcontent may have many medias.
 * you can find favlist in https://space.bilibili.com/your_uid/favlist in the left, and the default favlistcontent in the center.
 *
 * @author ZhiXingHeYi
 */
public class LocalInfoManager {

    /**
     * get personal favorite list.
     * This favlist does not contains medias.
     *
     * @param uid  uid
     * @param mode {@link UpdateMode}
     * @return json bean or null, cause null if json file cannot be analysed correctly.
     * @thread background
     */
    @Nullable
    public static FavListJsonBean getFavList(String uid, UpdateMode mode) {
        if (mode != UpdateMode.FORCE_LOCAL) {
            if (mode == UpdateMode.ONLINE || !isFileExists(GlobalVariables.FAVLIST_INDEX_FILE_NAME)) {
                Log.i(GlobalVariables.TAG, "联网获取FavList");
                Network.getFavListFile(uid);
            }
        }
        FavListJsonBean json = readJson(GlobalVariables.FAVLIST_INDEX_FILE_NAME, FavListJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "出现错误");
        }
        return json;
    }

    /**
     * getUserInfo Json bean.
     *
     * @param uid  uid
     * @param mode {@link UpdateMode}
     * @return {@link UserInfoJsonBean} entity class or null, cause null if json cannot be analysed correctly.
     * @thread background
     */
    @Nullable
    public static UserInfoJsonBean getUserInfo(String uid, UpdateMode mode) {
        if (mode != UpdateMode.FORCE_LOCAL) {
            if (mode == UpdateMode.ONLINE || !isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) {
                Log.i(GlobalVariables.TAG, "联网获取UserInfo");
                Network.getUserInfoFile(uid);
            }
        }
        UserInfoJsonBean json = readJson(GlobalVariables.USER_INFO_FILE_NAME, UserInfoJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "出现错误");
        }
        return json;
    }

    /**
     * getUserInfo quick
     *
     * @param mode update mode. uid can be ignored cause FORCE_LOCAL update will never use uid.
     * @return {@link #getUserInfo(String, UpdateMode)} )} param (-1, mode) or null if mode is not FORCE_LOCAL
     * @thread all
     */
    @Nullable
    public static UserInfoJsonBean getUserInfo(UpdateMode mode) {
        if (mode != UpdateMode.FORCE_LOCAL) {
            return null;
        }
        return getUserInfo("-1", UpdateMode.LOCAL);
    }

    /**
     * getUserface from local or online.
     *
     * @param link user face link. usually i0.***.com (decided by bilibili.com)
     * @param mode {@link UpdateMode}
     * @return Bitmap or null, cause null if file is still not exist after a download (maybe a failed download)
     * @thread background
     */
    @Nullable
    public static Bitmap getUserFace(String link, UpdateMode mode) {
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

    /**
     * get favlistcontent
     *
     * @param fid   fid
     * @param total total number of favlistcontent
     * @param mode  {@link UpdateMode}
     * @return Json bean or null, cause null if json cannot be analysed correctly.
     * @thread background
     */
    @Nullable
    public static FavlistContentJsonBean getFavListContent(String fid, int total, UpdateMode mode) {
        String fileName = GlobalVariables.FavListIndexFileName(fid);
        if (mode != UpdateMode.FORCE_LOCAL) {
            if (mode == UpdateMode.ONLINE || !isFileExists(fileName)) {
                Log.i(GlobalVariables.TAG, "联网获取收藏夹内容");
                Network.getFavListContent(fid, total);
            }
        }
        FavlistContentJsonBean json = readJson(fileName, FavlistContentJsonBean.class);
        if (json == null) {
            Log.e(GlobalVariables.TAG, "获取收藏夹失败");
        }
        return json;
    }

    /**
     * getMediaFile as songObject and UpdateMode.
     * cause null if {@link IOException} occured or the media file has not downloaded.
     *
     * @param songObject song
     * @param mode       {@link UpdateMode}
     * @return File of song
     * @thread background
     */
    @Nullable
    public static File getMediaFile(@NotNull SongObject songObject, UpdateMode mode) {
        return getMediaFile(songObject.getBvid(), songObject.getP(), mode);
    }

    /**
     * getMediaFile as bvid, p, and Updatemode
     * cause null if {@link IOException} occured or the media file has not downloaded.
     *
     * @param bvid bvid
     * @param p    分p
     * @param mode UpdateMode
     * @return File
     * @thread background
     */
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
            File media = new File(ApplicationMain.getContext().getFilesDir()
                    .getCanonicalPath().concat("/" + GlobalVariables.MediaFileName(bvid)));
            if (media.exists()) {
                return media;
            } else {
                EventBus.getDefault().post(new PlayerEvents.HintToast(R.string.t_download_first));
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read json as the fileName.
     *
     * @param fileName json file name
     * @param clazz    jsonbean class
     * @param <T>      jsonbean class
     * @return JsonBean or null, cause null if there is {@link IOException}
     * @thread all
     */
    @Nullable
    private static <T> T readJson(String fileName, Class<T> clazz) {
        try {
            FileInputStream in = ApplicationMain.getContext().openFileInput(fileName);
            String line;
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

    /**
     * Used to detect a file. Usually used in detecting whether the media file is not broken.
     *
     * @param fileName file name
     * @return {@link FileState}
     * @thread all
     */
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

    /**
     * @param fileName file name
     * @return true is file is exist, and false if file is not exist or cause {@link IOException}
     * @thread all
     */
    public static boolean isFileExists(String fileName) {
        try {
            return new File(ApplicationMain.getContext().getFilesDir().getCanonicalPath().concat("/").concat(fileName)).exists();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @see #getFileState(String)
     */
    enum FileState {
        EXIST,
        NOT_EXIST,
        SEEMS_BROKEN
    }
}
