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

import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.C;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;

import com.example.lab1.MainActivity;
import com.example.lab1.R;
import com.example.lab1.data.DBHelper;


import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


@UnstableApi
public class VideoPlayerFragment extends Fragment {
    private ExoPlayer player;
    private PlayerView playerView;

    private boolean audioOnly = false;

    private Handler sleepHandler = new Handler(Looper.getMainLooper());
    private Runnable sleepRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        playerView = view.findViewById(R.id.playerView);

        setHasOptionsMenu(true);

        String url = null;

        if (getArguments() != null) {
            url = getArguments().getString("url");
            audioOnly = getArguments().getBoolean("audio_only", false);
        }

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(requireContext());

        if (audioOnly) {
            trackSelector.setParameters(
                    trackSelector.buildUponParameters()
                            .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, true)
            );
        }

        player = new ExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .build();

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

            if (audioOnly) {
                playerView.setUseController(true);
                playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
            }

            player.prepare();
            player.setPlayWhenReady(true);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_video, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_timer_1) {
            startSleepTimer(1);
            return true;
        }
        else if (id == R.id.action_timer_5) {
            startSleepTimer(5);
            return true;
        }
        else if (id == R.id.action_timer_30) {
            startSleepTimer(30);
            return true;
        }
        else if (id == R.id.action_timer_off) {
            cancelSleepTimer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSleepTimer(int minutes) {
        cancelSleepTimer(); // jeśli już był

        sleepRunnable = () -> {
            if (player != null) {
                player.pause();
                Toast.makeText(requireContext(), "Playback stopped", Toast.LENGTH_SHORT).show();
            }
        };

        sleepHandler.postDelayed(sleepRunnable, minutes * 60 * 1000);
        Toast.makeText(requireContext(), "Sleep timer set for " + minutes + " min", Toast.LENGTH_SHORT).show();
    }

    private void cancelSleepTimer() {
        if (sleepRunnable != null) {
            sleepHandler.removeCallbacks(sleepRunnable);
            sleepRunnable = null;
            Toast.makeText(requireContext(), "Sleep timer cancelled", Toast.LENGTH_SHORT).show();
        }
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
            long pos = player.getCurrentPosition();
            player.release();

            String url = getArguments().getString("url");

            DBHelper db = new DBHelper(requireContext());
            db.updateLastWatched(url, System.currentTimeMillis() + "");
        }
    }
}