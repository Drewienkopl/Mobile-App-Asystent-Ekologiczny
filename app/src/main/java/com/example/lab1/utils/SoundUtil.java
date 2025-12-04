package com.example.lab1.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.lab1.R;

public class SoundUtil {
    public static void playConfirmSound(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.confirm_lightsaber);
        mp.setOnCompletionListener(MediaPlayer::release);
        mp.start();
    }
}
