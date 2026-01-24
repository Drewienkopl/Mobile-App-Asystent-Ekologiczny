package com.example.lab1.ui.video;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.example.lab1.MainActivity;
import com.example.lab1.R;

public class VideoPlayerFragment extends Fragment {
    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        playerView = view.findViewById(R.id.playerView);

        String url = getArguments() != null ? getArguments().getString("url") : null;

        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        playerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                // nic nie robimy, tylko zachowujemy kontrole widoczne
            }
        });

        playerView.findViewById(androidx.media3.ui.R.id.exo_fullscreen)
                .setOnClickListener(v -> {
                    requireActivity().setRequestedOrientation(
                            getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_PORTRAIT
                                    ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    );
                });

        if (url != null) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(url)
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build();

            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
        }

        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyFullscreen();
    }

    private void applyFullscreen() {
        if (getActivity() == null) {
            return;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Fullscreen
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getActivity().getWindow().setDecorFitsSystemWindows(false);
                getActivity().getWindow().getInsetsController()
                        .hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }

            // hide toolbar
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).getSupportActionBar().hide();
            }

        } else {
            //Normal mode
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getActivity().getWindow().getInsetsController().show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                getActivity().getWindow().setDecorFitsSystemWindows(true);
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_VISIBLE
                );
            }

            //show toolbar
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).getSupportActionBar().show();
            }
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }
}