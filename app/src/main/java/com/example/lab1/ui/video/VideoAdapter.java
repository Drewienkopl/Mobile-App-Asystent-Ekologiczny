package com.example.lab1.ui.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab1.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(VideoMaterial video);
    }

    private final List<VideoMaterial> videos;
    private final OnVideoClickListener listener;

    public VideoAdapter(List<VideoMaterial> videos, OnVideoClickListener listener) {
        this.videos = videos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoMaterial video = videos.get(position);

        holder.tvTitle.setText(video.getTitle());
        holder.tvDescription.setText(video.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnail())
                .into(holder.imgThumb);

        if (holder.tvLastWatched != null) {
            if (video.getLastWatched() != null && !video.getLastWatched().isEmpty()) {
                try {
                    long time = Long.parseLong(video.getLastWatched());
                    Date date = new Date(time);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                    holder.tvLastWatched.setText("Last watched: " + sdf.format(date));
                } catch (Exception e) {
                    holder.tvLastWatched.setText("Last watched: never");
                }
            }
            else
                holder.tvLastWatched.setText("Last watched: never");
        }

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLastWatched;
        ImageView imgThumb;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvLastWatched = itemView.findViewById(R.id.tvLastWatched);
        }
    }
}
