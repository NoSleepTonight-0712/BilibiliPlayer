package com.github.zhixinghey0712.bilibiliplayer.util.player;

import java.util.ArrayDeque;

import static com.github.zhixinghey0712.bilibiliplayer.util.PlayMode.*;

public class PlayListManager extends ArrayDeque<Music> {
    private boolean DequeFull = false;
    private final int DEQUE_MAX_LENGTH = 30;
    private int CurrentPlayIndex = 0;
    private com.github.zhixinghey0712.bilibiliplayer.util.PlayMode PlayMode = DEFAULT;

    public PlayListManager() {
        // TODO 从文件加载历史记录

        // 无历史记录

    }

    public void playNext() {

    }

    public void playPrevious() {
        if (this.peekFirst() == null) {
            this.addFirst(generatePlayListItem());
        }
        
    }

    /**
     * 返回下一次播放的歌（不再队内）
     * @return 接下来播放的歌 {@link Music}
     */
    private Music generatePlayListItem() {
        Music result = null;
        switch (PlayMode) {
            case DEFAULT:
                result = generatePlayListItemInDefaultMode();
                break;
            case RANDOM:
                result = generatePlayListItemInRandomMode();
                break;
            case SMART:
                result = generatePlayListItemInSmartMode();
                break;
        }
        return result;
    }

    private Music generatePlayListItemInDefaultMode() {
        return null;
    }

    private Music generatePlayListItemInRandomMode() {
        return null;

    }

    private Music generatePlayListItemInSmartMode() {
        return null;

    }
}
