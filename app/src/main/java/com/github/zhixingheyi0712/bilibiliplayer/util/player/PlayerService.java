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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class PlayerService extends Service {
    private final String TAG = GlobalVariables.TAG + ": PlayService";
    private MediaPlayer player;

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

        player = new MediaPlayer();
        player.setOnCompletionListener(mp -> playMusic(PlayListManager.nextPlay(false)));

        if (!EarphoneDisconnectListener.enabled){
            IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            registerReceiver(new EarphoneDisconnectListener(), filter);
        }
    }

    /**
     * This method only receive the stop playing intent.
     * {@link com.github.zhixingheyi0712.bilibiliplayer.ui.SettingsFragment}
     *
     * @param intent  stop playing intent
     * @param flags   auto
     * @param startId auto
     * @return auto
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(GlobalVariables.STOP_PLAYING, false)) {
            EventBus.getDefault().post(new PlayerEvents.SetPlayingServiceState());
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
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void switchPlayPauseService(@NotNull PlayerEvents.SetPlayingServiceState event) {
        if (event.isForcePauseEnabled()) {
            if (player.isPlaying()) {
                player.pause();
            }
            EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingButtonState(player.isPlaying()));
            return;
        }

        // 没有加载音频就退出
        int length = player.getDuration();
        if (length < 0) return;

        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
        EventBus.getDefault().postSticky(new PlayerEvents.SetPlayingButtonState(player.isPlaying()));
        EventBus.getDefault().removeStickyEvent(event);
    }

    /**
     * set player resource. the event contains a song.
     *
     * @param event {@link PlayerEvents.SetPlayerResource}
     * @see EventBus
     */
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
                Toast.makeText(ApplicationMain.getContext(),
                        R.string.t_filebroken, Toast.LENGTH_SHORT).show();
                boolean _ignore = f.delete();
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "file is not exist.");
        }
    }

    /**
     * update play/pause button
     * always be sent by ui.
     *
     * @param event {@link PlayerEvents.ResendUpdatePlayPauseButton}
     */
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
}
