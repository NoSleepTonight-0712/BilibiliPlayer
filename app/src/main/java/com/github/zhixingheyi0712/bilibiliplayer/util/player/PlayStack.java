package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import androidx.annotation.Nullable;

import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;

import java.util.ArrayDeque;

public class PlayStack extends ArrayDeque<SongObject> {
    private int limit;

    public PlayStack() {
        super();
        limit = 15;
    }

    public PlayStack(int elements) {
        super(elements);
        limit = elements;
    }

    @Override
    public void push(SongObject songObject) {
        if (songObject == null) return;
        if (size() >= limit) {
            // 栈满
            pollFirst();
        }
        addLast(songObject);
    }

    @Nullable
    @Override
    public SongObject pop() {
        return pollLast();
    }
}
