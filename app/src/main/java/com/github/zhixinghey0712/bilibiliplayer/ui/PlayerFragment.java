package com.github.zhixinghey0712.bilibiliplayer.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.zhixinghey0712.bilibiliplayer.MainActivity;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayListHistory;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayListManager;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayerBinder;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayerService;

import org.w3c.dom.Text;

import java.util.Objects;

public class PlayerFragment extends Fragment {
    private ImageButton play;
    private ImageButton forward;
    private ImageButton backward;

    private PlayerBinder.PlayBinderPointer pBinder;
    private SongObject old_currentSong;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000);// 间隔120秒
        }
        void update() {
            if (old_currentSong == PlayListManager.getCurrentSong()) return;
            updateTitleBox();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_player, container, false);
        play = mainView.findViewById(R.id.play_button);
        forward = mainView.findViewById(R.id.forward_button);
        backward = mainView.findViewById(R.id.backward_button);

        play.setOnClickListener(v -> sendPlayPauseSwitch());

        forward.setOnClickListener(v -> {
            Intent forwardIntent = new Intent(requireActivity(), PlayerService.class);
            forwardIntent.putExtra(GlobalVariables.CHANGE_MUSIC_TO_SERVICE, GlobalVariables.FORWARD_MUSIC_TO_SERVICE);
            requireActivity().startForegroundService(forwardIntent);
        });

        backward.setOnClickListener(v -> {
            Intent backwardIntent = new Intent(requireActivity(), PlayerService.class);
            backwardIntent.putExtra(GlobalVariables.CHANGE_MUSIC_TO_SERVICE, GlobalVariables.BACKWARD_MUSIC_TO_SERVICE);
            requireActivity().startForegroundService(backwardIntent);
        });

        handler.postDelayed(runnable, 1000);

        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pBinder = ((MainActivity) requireActivity()).getpBinder();
        try {
            updateTitleBox();
        } catch (NullPointerException ignore) {
        }
        updatePlayPauseButton();
    }

    private void updateTitleBox() {
        String titleText = PlayListManager.getCurrentSong().getName() + " - " + PlayListManager.getCurrentSong().getSinger();
        Activity activity = getActivity();
        if (activity == null) return;
        TextView title = activity.findViewById(R.id.player_music_name);
        title.setText(titleText);
        title.setSelected(true);
    }

    private void updatePlayPauseButton() {
        if (pBinder == null) return;
        PlayerBinder binder = pBinder.getBinder();
        if (binder == null) return;

        if (!pBinder.getBinder().getPlayer().isPlaying()) {
            play.setImageResource(R.drawable.ic_play);
        } else {
            play.setImageResource(R.drawable.ic_pause);
        }
    }

    private void sendPlayPauseSwitch() {
        if (pBinder.getBinder().isPrepared()) {
            if (pBinder.getBinder().getPlayer().isPlaying()) {
                play.setImageResource(R.drawable.ic_play);
            } else {
                play.setImageResource(R.drawable.ic_pause);
            }
            Intent playPauseSwitch = new Intent(requireActivity(), PlayerService.class);
            playPauseSwitch.putExtra(GlobalVariables.SWITCH_PLAY_PAUSE_TO_SERVICE, true);
            requireActivity().getApplicationContext().startForegroundService(playPauseSwitch);
        }
    }


}