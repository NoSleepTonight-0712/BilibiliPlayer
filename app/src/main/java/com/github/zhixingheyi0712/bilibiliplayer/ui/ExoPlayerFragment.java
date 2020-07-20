package com.github.zhixingheyi0712.bilibiliplayer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayListManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerEvents;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerService;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public class ExoPlayerFragment extends Fragment {
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private SongObject currentSong;

    @Override
    @Subscribe
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exo_player, container, false);
        if (PlayerService.getExoPlayer() == null) {
            PlayerService.setExoPlayer(new SimpleExoPlayer.Builder(getContext()).build());
        }
        player = PlayerService.getExoPlayer();

        PlayerControlView controlView = view.findViewById(R.id.bilibili_exo_player_controler);
        controlView.setPlayer(player);
        return view;
    }


    /**
     * set title box when back from {@link FavListContentAdapter#onCreateViewHolder(ViewGroup, int)}
     * This method is used to update UI when favlist content is selected.
     *
     * @param savedInstanceState auto
     * @see #updateTitleBox(PlayerEvents.SetPlayingInfo)
     * @see FavListContentAdapter#onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.exoplayer_music_name);
        if (currentSong != null) {
            title.setText(currentSong.getInfoString());
            title.setSelected(true);
        }
    }

    /**
     * update Title box content.
     * There are 2 cases when update:
     * <ul>
     *     <li>
     *         when user click next button or the song is finished:<br>
     *         In this cases, title can be find using <code>getActivity().findViewById()</code>
     *         So this method will be work correctly.
     *     </li>
     *     <li>
     *         when user select a song in favlist content and back to {@link ExoPlayerFragment}, this method
     *         will be called first, and {@link #onActivityCreated(Bundle)} will be called later. So title will
     *         be <code>null</code> at this case.
     *         However, the event is sent by {@link FavListContentAdapter#onCreateViewHolder(ViewGroup, int)},
     *         and currentSong will be set. So the UI change will happen in {@link #onActivityCreated(Bundle)}
     *     </li>
     * </ul>
     *
     * @param event Event sent from {@link PlayListManager#updatePlayList()} when
     *              ask for next song, and sent from
     *              {@link FavListContentAdapter#onCreateViewHolder(ViewGroup, int)} when favlist content
     *              is selected.
     * @see #onActivityCreated(Bundle)
     * @see FavListContentAdapter#onCreateViewHolder(ViewGroup, int)
     * @see PlayListManager#updatePlayList()
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateTitleBox(@NotNull PlayerEvents.SetPlayingInfo event) {
        TextView title = getActivity().findViewById(R.id.exoplayer_music_name);
        currentSong = event.getSong();
        if (title == null) return;
        title.setText(currentSong.getInfoString());
        EventBus.getDefault().removeStickyEvent(event);
        title.setSelected(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}