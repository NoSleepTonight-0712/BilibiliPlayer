package com.github.zhixinghey0712.bilibiliplayer.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.MainActivity;
import com.github.zhixinghey0712.bilibiliplayer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayerFragment extends Fragment {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_player, container, false);
        ImageButton play = mainView.findViewById(R.id.play_button);
        return mainView;
    }

    private void initMediaPlayer() {

    }
}