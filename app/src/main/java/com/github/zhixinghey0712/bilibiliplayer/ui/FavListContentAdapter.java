package com.github.zhixinghey0712.bilibiliplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixinghey0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayListManager;
import com.github.zhixinghey0712.bilibiliplayer.util.player.PlayerService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FavListContentAdapter extends RecyclerView.Adapter<FavListContentAdapter.ViewHolder> {
    private List<SongObject> songObjects;
    private Activity activity;

    public FavListContentAdapter(List<SongObject> songs, Activity activity) {
        songObjects = songs;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_content_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.favlistContentView.setOnClickListener(v -> {
            int position = holder.getLayoutPosition();
            SongObject song = songObjects.get(position);
            Log.i(GlobalVariables.TAG, "Set current song when click in favlist activity: " + song.toString());
            PlayListManager.setCurrentSong(song);

            Intent sentSongIntent = new Intent(activity, PlayerService.class);
            sentSongIntent.putExtra(GlobalVariables.PLAY_RESOURCE, song);
            activity.startService(sentSongIntent);

            activity.finish();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongObject song = songObjects.get(position);
        holder.name.setText(song.getName());
        holder.singer.setText(song.getSinger());
    }

    @Override
    public int getItemCount() {
        return songObjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View favlistContentView;
        TextView name;
        TextView singer;
        public ViewHolder(@NonNull View view) {
            super(view);
            favlistContentView = view;
            name = view.findViewById(R.id.playlist_content_item_name);
            singer = view.findViewById(R.id.playlist_content_item_singer);
        }
    }
}
