package com.example.lab1.ui.video;

import android.media.browse.MediaBrowser;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.media3.ui.PlayerView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;

import java.util.List;

public class VideoListFragment extends Fragment {

    RecyclerView recyclerView;
    VideoAdapter videoAdapter;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        db = new DBHelper(requireContext());

        if (db.getVideos().isEmpty()) {
            List<VideoMaterial> json = new VideoMaterial("", "", "", "")
                    .loadVideoMaterialsFromJson(requireContext());

            for (VideoMaterial v : json) {
                db.insertVideo(v);
            }
        }

        recyclerView = view.findViewById(R.id.recyclerVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fabAddVideo);

        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_videoList_to_addVideo);
        });

        List<VideoMaterial> videos = db.getVideos();

        videoAdapter = new VideoAdapter(videos, new VideoAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(VideoMaterial video) {
                Bundle b = new Bundle();
                b.putString("url", video.getUrl());
                b.putBoolean("audio_only", false);

                NavHostFragment.findNavController(VideoListFragment.this)
                        .navigate(R.id.action_videoList_to_player, b);
            }

            @Override
            public void onAudioOnlyClick(VideoMaterial video) {
                Bundle b = new Bundle();
                b.putString("url", video.getUrl());
                b.putBoolean("audio_only", true);

                NavHostFragment.findNavController(VideoListFragment.this)
                        .navigate(R.id.action_videoList_to_player, b);
            }
        });


        recyclerView.setAdapter(videoAdapter);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        videoAdapter = new VideoAdapter(db.getVideos(), new VideoAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(VideoMaterial video) {
                Bundle b = new Bundle();
                b.putString("url", video.getUrl());
                b.putBoolean("audio_only", false);

                NavHostFragment.findNavController(VideoListFragment.this)
                        .navigate(R.id.action_videoList_to_player, b);
            }

            @Override
            public void onAudioOnlyClick(VideoMaterial video) {
                Bundle b = new Bundle();
                b.putString("url", video.getUrl());
                b.putBoolean("audio_only", true);

                NavHostFragment.findNavController(VideoListFragment.this)
                        .navigate(R.id.action_videoList_to_player, b);
            }
        });

        recyclerView.setAdapter(videoAdapter);
    }
}