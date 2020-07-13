package com.github.zhixinghey0712.bilibiliplayer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;

import java.util.List;

public class FavListContentAdapter extends RecyclerView.Adapter<FavListContentAdapter.ViewHolder> {
    private List<SongObject> songObjects;

    public FavListContentAdapter(List<SongObject> songs) {
        songObjects = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_content_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

//        holder.favListContentView.setOnClickListener(v -> {
//            int position = holder.getLayoutPosition();
//            SongObject favList = songObjects.get(position);
//
//            // TODO 点击播放
//        });

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
//        View favListContentView;
        TextView name;
        TextView singer;
        public ViewHolder(@NonNull View view) {
            super(view);
//            favListContentView = view;
            name = view.findViewById(R.id.playlist_content_item_name);
            singer = view.findViewById(R.id.playlist_content_item_singer);
        }
    }
}
