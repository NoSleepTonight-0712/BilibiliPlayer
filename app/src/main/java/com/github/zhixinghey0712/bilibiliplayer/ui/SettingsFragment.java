package com.github.zhixinghey0712.bilibiliplayer.ui;

import android.icu.lang.UScript;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.PlayMode;
import com.github.zhixinghey0712.bilibiliplayer.util.UserSettings;

import javax.crypto.SecretKey;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
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
            Toast.makeText(getActivity(), "当前播放模式：" + key, Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}