package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.app.Application;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.zhixingheyi0712.bilibiliplayer.ApplicationMain;
import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;

import org.greenrobot.eventbus.EventBus;

public class PlayListHistory {
    private int LIMIT = 20;
    private PlayStack previous = new PlayStack(LIMIT);
    private PlayStack future = new PlayStack(LIMIT);

    public SongObject finish(SongObject song) {
        previous.push(song);
        SongObject result = future.pop();
        if (result == null) {
            addToFuture();
            result = future.pop();
        }
        return result;
    }

    public SongObject back(SongObject song) {
        SongObject result = previous.pop();
        if (result == null) {
            EventBus.getDefault().post(new PlayerEvents.HintToast(R.string.t_nothing_in_history));
            return null;
        }
        future.push(song);
        return result;
    }

    private void addToFuture() {
        PlayMode mode = UserSettings.getPlayMode();
        future.addFirst(PlayListManager.getNextSong(mode));
    }
}
