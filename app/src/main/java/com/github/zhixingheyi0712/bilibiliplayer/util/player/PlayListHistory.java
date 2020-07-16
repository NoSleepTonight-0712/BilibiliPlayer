package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import androidx.annotation.NonNull;

import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;

import org.greenrobot.eventbus.EventBus;

public class PlayListHistory {
    private int LIMIT = 20;
    private PlayStack previous = new PlayStack(LIMIT);
    private PlayStack future = new PlayStack(LIMIT);

    /**
     * called when a song is finished.
     * @param song current song
     * @return next song
     */
    public SongObject finish(SongObject song) {
        previous.push(song);
        SongObject result = future.pop();
        if (result == null) {
            addToFuture();
            result = future.pop();
        }
        return result;
    }

    /**
     * call when user press the previous button.
     * @param song current song
     * @return previous song
     */
    public SongObject back(@NonNull SongObject song) {
        SongObject result = previous.pop();
        if (result == null) {
            EventBus.getDefault().post(new PlayerEvents.HintToast(R.string.t_nothing_in_history));
            return null;
        }
        future.push(song);
        return result;
    }

    /**
     * this will be called when {@link #future} is empty.
     */
    private void addToFuture() {
        PlayMode mode = UserSettings.getPlayMode();
        SongObject song = PlayListManager.getNextSong(mode);
        if (song == null) return;
        future.addFirst(song);
    }

    /**
     * this will be called when the playmode is changed.
     */
    public void clearFutureStack() {
        future.clear();
    }
}
