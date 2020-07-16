package com.github.zhixingheyi0712.bilibiliplayer.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayListManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public class PlayerFragment extends Fragment {
    private ImageButton play;
    private ImageButton next;
    private ImageButton previous;

    @Subscribe
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_player, container, false);
        play = mainView.findViewById(R.id.play_button);
        next = mainView.findViewById(R.id.forward_button);
        previous = mainView.findViewById(R.id.backward_button);
        EventBus.getDefault().register(this);

        play.setOnClickListener(v -> {
            EventBus.getDefault().post(new PlayerEvents.SetPlayingServiceState());
        });

        next.setOnClickListener(v -> {
            EventBus.getDefault().post(new PlayerEvents.PlayNextSong(false));
        });

        previous.setOnClickListener(v -> {
            EventBus.getDefault().post(new PlayerEvents.PlayNextSong(true));
        });

        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            updateTitleBox(getActivity().findViewById(R.id.player_music_name), PlayListManager.getCurrentSong().getInfoString());
        } catch (NullPointerException ignore) {}
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateTitleBoxEventListener(PlayerEvents.SetPlayingInfo event) {
        Activity activity = getActivity();
        if (activity != null) {
            updateTitleBox(activity.findViewById(R.id.player_music_name), event.getSong().getInfoString());
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void updateTitleBox(TextView textBox, String info) {
        if (textBox == null) return;
        textBox.setText(info);
        textBox.setSelected(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updatePlayPauseButton(@NotNull PlayerEvents.SetPlayingButtonState event) {
        if (!event.isPlaying()) {
            play.setImageResource(R.drawable.ic_play);
        } else {
            play.setImageResource(R.drawable.ic_pause);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}