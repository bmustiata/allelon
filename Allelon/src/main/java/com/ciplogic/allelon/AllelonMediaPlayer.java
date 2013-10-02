package com.ciplogic.allelon;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class AllelonMediaPlayer {
    private MediaPlayer mediaPlayer;

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void startPlay() {
        if (isPlaying()) {
            stopPlay();
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("http://87.118.82.77:8000/stream/1/");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
