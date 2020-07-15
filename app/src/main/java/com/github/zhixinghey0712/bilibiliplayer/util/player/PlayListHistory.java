package com.github.zhixinghey0712.bilibiliplayer.util.player;

import android.widget.Toast;

import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.PlayMode;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.UserSettings;

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
            Toast.makeText(ApplicationMain.getContext(), R.string.t_nothing_in_history, Toast.LENGTH_SHORT).show();
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
