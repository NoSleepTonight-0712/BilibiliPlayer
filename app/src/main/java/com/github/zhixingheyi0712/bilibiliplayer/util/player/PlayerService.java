package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.github.zhixingheyi0712.bilibiliplayer.ApplicationMain;
import com.github.zhixingheyi0712.bilibiliplayer.MainActivity;
import com.github.zhixingheyi0712.bilibiliplayer.R;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.info.LocalInfoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PlayerService extends Service {
    private final String TAG = GlobalVariables.TAG + ": PlayService";
    private MediaPlayer player;
    private PlayerBinder binder = new PlayerBinder();

    @Override
    @Subscribe
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        Log.i(TAG, "onCreate");
        String ID = "com.github.zhixingheyi0712.bilibili";    //这里的id里面输入自己的项目的包的路径
        String NAME = "播放器";
        Intent intent = new Intent(PlayerService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification; //创建服务对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        Objects.requireNonNull(manager).createNotificationChannel(channel);
        notification = new NotificationCompat.Builder(PlayerService.this, ID);

        notification.setContentTitle("Bilibili Player")
                .setContentText("内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSound(null)
                .setContentIntent(pendingIntent)
                .build();
        Notification notification1 = notification.build();
        startForeground(1, notification1);

        player = new MediaPlayer();
        player.setOnCompletionListener(mp -> playMusic(PlayListManager.nextPlay(false)));
        binder.setPlayer(player);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void switchPlayPauseService(PlayerEvents.SetPlayingServiceState event) {
        if (player.isPlaying()) {
            player.pause();
            EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingButtonState(false));
        } else {
            player.start();
            EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingButtonState(true));
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void setPlayerResource(PlayerEvents.SetPlayerResource event) {
        playMusic(event.getSong());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void playNextSong(PlayerEvents.PlayNextSong event) {
        if (event.isPrevious()) {
            playMusic(PlayListManager.nextPlay(true));
        } else {
            playMusic(PlayListManager.nextPlay(false));
        }
    }

    private void playMusic(@Nullable SongObject song) {
        if (song == null) return;
        File f = LocalInfoManager.getMediaFile(song, UpdateMode.FORCE_LOCAL);
        if (f != null) {
            try {
                player.reset();
                player.setDataSource(f.getPath());
                player.prepareAsync();
                EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingInfo(song));
                PlayListManager.setCurrentSong(song);
                player.setOnPreparedListener(MediaPlayer::start);
            } catch (IOException e) {
                Toast.makeText(ApplicationMain.getContext(), R.string.t_filebroken, Toast.LENGTH_SHORT).show();
                f.delete();
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "file is not exist.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        player = null;
    }
}
