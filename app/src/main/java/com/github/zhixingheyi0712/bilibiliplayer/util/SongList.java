package com.github.zhixingheyi0712.bilibiliplayer.util;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.text.SubtitleOutputBuffer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SongList implements Serializable {
    private ArrayList<SongObject> songObjects = new ArrayList<>();
    private String name;
    private String fid;

    /**
     * use {@link #getNextSong(PlayMode, int)} instead.
     * @param mode mode
     * @param indexInList index
     * @return ArrayList
     */
    public ArrayList<SongObject> getSongSequence(PlayMode mode, int indexInList) {
        ArrayList<SongObject> result = new ArrayList<>();
        if (mode == PlayMode.DEFAULT || mode == PlayMode.LOOP) {
            if (indexInList + 10 < songObjects.size() - 1) {
                // 取连续的10个
                for (int i = indexInList + 1; i <= indexInList + 10; i++) {
                    result.add(songObjects.get(i));
                }
            } else {
                // 结尾段
                int addTimes = 0;
                for (int i = indexInList + 1; i < songObjects.size(); i++) {
                    result.add(songObjects.get(i));
                    addTimes++;
                }
                for (int i = 0; i < 10 - addTimes; i++) {
                    result.add(songObjects.get(i));
                    addTimes++;
                }
            }
        } else {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                int r_index = random.nextInt(songObjects.size() - 1);
                result.add(songObjects.get(r_index));
            }
        }
        return result;
    }

    /**
     * get the next song which will be played (if user do not click the previous button)
     * @param mode playmode
     * @param indexInList this param is only used when mode was set as LOOP or DEFAULT.
     *                    It is used to find which is the next. Cause RANDOM or SMART
     *                    has not definitely 'next', so the param is useless then.
     * @return
     */
    public SongObject getNextSong(PlayMode mode, int indexInList) {
        if (mode == PlayMode.DEFAULT || mode == PlayMode.LOOP) {
            if (indexInList == songObjects.size() - 1) {
                return songObjects.get(0);
            } else {
                return songObjects.get(indexInList + 1);
            }
        } else {
            Random random = new Random();
            int r_index = random.nextInt(songObjects.size() - 1);
            return songObjects.get(r_index);
        }
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SongObject> getSongObjects() {
        return songObjects;
    }

    public void setSongObjects(ArrayList<SongObject> songObjects) {
        this.songObjects = songObjects;
    }

    public int getSize() {
        return this.songObjects.size();
    }

    @Override
    public SongList clone() {
        SongList songList = new SongList();
        songList.setFid(this.fid);
        songList.setName(this.name);
        songList.setSongObjects((ArrayList<SongObject>) this.songObjects.clone());
        return songList;
    }
}
