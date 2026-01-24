package com.example.lab1.ui.video;

import android.media.browse.MediaBrowser;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.media3.ui.PlayerView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab1.R;

import java.util.List;

public class VideoListFragment extends Fragment {

    RecyclerView recyclerView;
    VideoAdapter videoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<VideoMaterial> videos =
                new VideoMaterial("", "", "").loadVideoMaterialsFromJson(requireContext());

        videoAdapter = new VideoAdapter(videos, video -> {
            Bundle b = new Bundle();
            b.putString("url", video.getUrl());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_videoList_to_player, b);
        });

        recyclerView.setAdapter(videoAdapter);
        return view;
    }
}