package com.github.zhixingheyi0712.bilibiliplayer.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayListManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerService;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RadioGroup playmodeGroup = view.findViewById(R.id.settings_play_mode);
        switch (UserSettings.getPlayMode()) {
            case RANDOM:
                playmodeGroup.check(R.id.settings_play_mode_random);
                break;
            case SMART:
                playmodeGroup.check(R.id.settings_play_mode_smart);
                break;
            case LOOP:
                playmodeGroup.check(R.id.settings_play_mode_loop);
                break;
            default:
                playmodeGroup.check(R.id.settings_play_mode_default);
                break;
        }
        playmodeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton select = view.findViewById(checkedId);
            String key = select.getText().toString();
            switch (key) {
                case "随机播放":
                    UserSettings.setPlayMode(PlayMode.RANDOM);
                    break;
                case "调平播放":
                    UserSettings.setPlayMode(PlayMode.SMART);
                    break;
                case "单曲循环":
                    UserSettings.setPlayMode(PlayMode.LOOP);
                    break;
                default:
                    UserSettings.setPlayMode(PlayMode.DEFAULT);
                    break;
            }
            PlayListManager.clearFutureStack();
            Toast.makeText(getActivity(), "当前播放模式：" + key, Toast.LENGTH_SHORT).show();
        });

        // 定时播放设置
        Button confirmPlayingTimeSet = view.findViewById(R.id.settings_playing_time_confirm_button);
        confirmPlayingTimeSet.setOnClickListener(v -> {
            try {
                TextView number = getActivity().findViewById(R.id.settings_playing_time);
                long time = Long.parseLong(number.getText().toString()) * 60 * 1000;
                AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), PlayerService.class);
                intent.putExtra(GlobalVariables.STOP_PLAYING, true);
                PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, intent, 0);
                alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent);
                Toast.makeText(getContext(), R.string.t_set_alarm_success, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException ignore) {
            }
        });

        return view;
    }
}