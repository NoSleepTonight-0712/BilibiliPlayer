package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.github.zhixingheyi0712.bilibiliplayer.ApplicationMain;
import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongList;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;
import com.github.zhixingheyi0712.bilibiliplayer.util.info.LocalInfoManager;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables.TAG;

/**
 * this class is used to control which to play next.
 * {@link #playlist} is the playlist used by {@link PlayerService}.
 * it can be modified and update.
 * {@link #songList} is used to generate and update the playlist.
 * usually stable if user do not refresh the {@link com.github.zhixingheyi0712.bilibiliplayer.FavListContentActivity}
 * <p>
 * I use {@link SongList#getNextSong(PlayMode, int)} to generate which to play next,
 * and use {@link #updatePlayList()} to put it to player in {@link PlayerService}
 *
 * @author ZhiXingHeYi
 */
public class PlayListManager {
    private static ConcatenatingMediaSource playlist = new ConcatenatingMediaSource();
    private static SongList songList;

    @Deprecated
    private static SongObject currentSong;

    @Deprecated
    private static PlayListHistory history = new PlayListHistory();

    /**
     * this method will always called when next song was ordered.
     * Including user click "next" button and the song is over.
     */
    public static void updatePlayList() {
        PlayMode mode = UserSettings.getPlayMode();
        int size = playlist.getSize();
        if (size == 0) return;
        MediaTag tag = ((MediaTag) playlist.getMediaSource(size - 1).getTag());
        int currentIndex;
        if (tag == null) {
            currentIndex = 0;
        } else {
            currentIndex = tag.getIndexInSongList();
        }

        // 单曲到最后一首也要是REPEAT_MODE_ALL
        if (mode == PlayMode.LOOP && PlayerService.getExoPlayer().getCurrentWindowIndex() != playlist.getSize() - 1) {
            PlayerService.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
        } else {
            PlayerService.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
        }

        if (playlist.getSize() < songList.getSize()) {
            // songlist中还有歌没有加入playlist
            SongObject nextSong = songList.getNextSong(mode, currentIndex);
            playlist.addMediaSource(getMediaSourceFromSongObject(nextSong));
        }

        MediaTag currentTag = (MediaTag) PlayerService.getExoPlayer().getCurrentTag();

        // 手动或自动下一首
        if (currentTag != null)
            EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingInfo(currentTag.getSongObject()));
    }

    /**
     * this method was only called in {@link com.github.zhixingheyi0712.bilibiliplayer.ui.FavListContentAdapter}
     * when a song is clicked.
     * After that, {@link #updatePlayList()} will be called.
     *
     * @param song clicked song in {@link com.github.zhixingheyi0712.bilibiliplayer.ui.ExoPlayerFragment}
     */
    public static void addToPlayList(SongObject song) {
        MediaSource source = getMediaSourceFromSongObject(song);
        if (source == null) {
            Toast.makeText(ApplicationMain.getContext(), R.string.t_please_login, Toast.LENGTH_SHORT).show();
            return;
        }
        playlist.addMediaSource(source);
    }

    /**
     * Convert SongObject to MediaSource.
     * ONLY MediaSource can be added to {@link #playlist}
     *
     * @param song {@link SongObject}
     * @return {@link MediaSource}
     * @see ConcatenatingMediaSource
     */
    @Nullable
    public static MediaSource getMediaSourceFromSongObject(SongObject song) {
        if (song == null) return null;
        File f = LocalInfoManager.getMediaFile(song, UpdateMode.FORCE_LOCAL);
        if (f != null) {
            return new ProgressiveMediaSource.Factory(PlayerService.getDataSourceFactory())
                    .setTag(new MediaTag(song))
                    .createMediaSource(Uri.parse(LocalInfoManager.getMediaFile(song, UpdateMode.LOCAL).getPath()));
        } else {
            Log.e(TAG, "file is not exist.");
            return null;
        }
    }

    @Deprecated
    public static void setCurrentSong(SongObject currentSong) {
        PlayListManager.currentSong = currentSong;
    }

    @Deprecated
    public static SongObject getCurrentSong() {
        return currentSong;
    }

    @Deprecated
    public static SongObject nextPlay(boolean isPrevious) {
        SongObject result;
        if (isPrevious) {
            result = history.back(currentSong);
        } else {
            result = history.finish(currentSong);
        }
        if (result != null) currentSong = result;
        return result;
    }

    /**
     * the main method to getNextSong.
     * <em>notice: the "next" is really the next.</em>
     * It will be called if a song is finished, or the user press the "next song" button.
     * The actual logic is in the following methods:
     *
     * @param mode mode
     * @return song
     * @see #nextSongInDefault()
     * @see #nextSongInRandom()
     * @see #nextSongInSmart()
     * @see #nextSongInLoop()
     * @see #updatePlayList()
     * @deprecated {@link SongList} instead.
     */
    @Deprecated
    @Nullable
    protected static SongObject getNextSong(@NotNull PlayMode mode) {
        if (songList == null) return null;
        SongObject result;
        switch (mode) {
            case RANDOM:
                result = nextSongInRandom();
                break;
            case SMART:
                result = nextSongInSmart();
                break;
            case LOOP:
                result = nextSongInLoop();
                break;
            default:
                result = nextSongInDefault();
                break;
        }
        return result;
    }

    /**
     * will be called if the user press the "previous song" button.
     * this method will get a history in {@link PlayStack}
     *
     * @return song
     */
    @Deprecated
    private static SongObject nextSongInDefault() {
        int index = songList.getSongObjects().indexOf(currentSong);
        SongObject result;
        if (index == songList.getSize() - 1) {
            result = songList.getSongObjects().get(0);
        } else {
            result = songList.getSongObjects().get(index + 1);
        }
        currentSong = result;
        return result;
    }

    @Deprecated
    @Nullable
    private static SongObject nextSongInRandom() {
        Random random = new Random();
        int r_index = random.nextInt(songList.getSize() - 1);
        return songList.getSongObjects().get(r_index);
    }

    @Deprecated
    private static SongObject nextSongInSmart() {
        // TODO
        return currentSong;
    }

    @Deprecated
    private static SongObject nextSongInLoop() {
        return currentSong;
    }

    public static void clearPlayList() {
        playlist.clear();
    }

    public static void clearFuturePlayList() {
        int size = playlist.getSize();
        int currentIndex = PlayerService.getExoPlayer().getCurrentWindowIndex();
        if (currentIndex == size - 1) return; // 已经是最后，不需要移除
        if (size != 1) playlist.removeMediaSourceRange(currentIndex + 1, playlist.getSize() - 1);
    }

    @Deprecated
    public static void clearFutureStack() {
        history.clearFutureStack();
    }

    public static ConcatenatingMediaSource getPlaylist() {
        return playlist;
    }

    public static void setSongList(SongList songList) {
        PlayListManager.songList = songList;
    }
}
