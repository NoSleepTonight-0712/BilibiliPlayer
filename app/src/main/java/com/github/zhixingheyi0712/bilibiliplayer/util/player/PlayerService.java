package com.github.zhixingheyi0712.bilibiliplayer.util.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

// start in MainActivity
public class PlayerService extends Service {
    private final String TAG = GlobalVariables.TAG + ": PlayService";
    @Deprecated
    private MediaPlayer player;
    private static SimpleExoPlayer exoPlayer;
    private static DataSource.Factory dataSourceFactory;

    @Override
    @Subscribe
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        Log.i(TAG, "onCreate");
        String ID = "com.github.zhixingheyi0712.bilibili";
        String NAME = "播放器";
        Intent intent = new Intent(PlayerService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification;
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
        if (exoPlayer == null)
            exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        if (dataSourceFactory == null) dataSourceFactory = new DefaultDataSourceFactory
                (getApplicationContext(), Util.getUserAgent(getApplicationContext(), "bilibili player"));
        exoPlayer.prepare(PlayListManager.getPlaylist());
        exoPlayer.addListener(new Player.EventListener() {
            /**
             * 手动点下一个：1
             * 自动：0
             * @param reason
             */
            @Override
            public void onPositionDiscontinuity(int reason) {
                if (reason == 0 || reason == 1) {
                    PlayListManager.updatePlayList();
                }
            }
        });

        // 动态注册耳机掉线监听器
        if (!EarphoneDisconnectListener.enabled) {
            IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            registerReceiver(new EarphoneDisconnectListener(), filter);
        }
    }

    /**
     * This method only receive the stop playing intent.
     * {@link com.github.zhixingheyi0712.bilibiliplayer.ui.SettingsFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     *
     * @param intent  stop playing intent
     * @param flags   auto
     * @param startId auto
     * @return auto
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(GlobalVariables.STOP_PLAYING, false)) {
            exoPlayer.setPlayWhenReady(false);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * {@link EventBus}
     * switch player playing or pause.
     * will resent a sticky post to update play/pause button.
     *
     * @param event {@link PlayerEvents.SetPlayingServiceState}
     * @see com.github.zhixingheyi0712.bilibiliplayer.ui.PlayerFragment
     */
    @Deprecated
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void switchPlayPauseService(@NotNull PlayerEvents.SetPlayingServiceState event) {
        if (event.isForcePauseEnabled()) {
            if (exoPlayer.isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
            }
            return;
        }

        // 没有加载音频就退出
        long length = exoPlayer.getDuration();
        if (length < 0) return;

        if (exoPlayer.isPlaying()) {
            exoPlayer.setPlayWhenReady(false);
        } else {
            exoPlayer.setPlayWhenReady(true);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    /**
     * set player resource. the event contains a song.
     *
     * @param event {@link PlayerEvents.SetPlayerResource}
     * @see EventBus
     */
    @Deprecated
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void setPlayerResource(PlayerEvents.SetPlayerResource event) {
        playMusic(event.getSong());
    }

    /**
     * playNextSong.
     * Notice the "next" means the next song to play, it maybe the previous one if event.isPrevious() == true.
     *
     * @param event {@link PlayerEvents.PlayNextSong}
     */
    @Deprecated
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void playNextSong(PlayerEvents.PlayNextSong event) {
        if (event.isPrevious()) {
            playMusic(PlayListManager.nextPlay(true));
        } else {
            playMusic(PlayListManager.nextPlay(false));
        }
    }

    /**
     * play music by SongObject.
     * call this method to play a music.
     *
     * @param song song
     */
    @Deprecated
    private void playMusic(@Nullable SongObject song) {
        MediaSource mediaSource = PlayListManager.getMediaSourceFromSongObject(song);
        if (mediaSource == null) return;
        exoPlayer.prepare(mediaSource);
    }

    /**
     * update play/pause button
     * always be sent by ui.
     *
     * @param event {@link PlayerEvents.ResendUpdatePlayPauseButton}
     */
    @Deprecated
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resendUpdatePlayPauseButton(PlayerEvents.ResendUpdatePlayPauseButton event) {
        EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingButtonState(player.isPlaying()));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        player = null;
    }

    public static void setExoPlayer(SimpleExoPlayer exoPlayer) {
        PlayerService.exoPlayer = exoPlayer;
    }

    public static SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public static DataSource.Factory getDataSourceFactory() {
        return dataSourceFactory;
    }
}
