package com.ciplogic.allelon;

import android.media.AudioManager;
import android.media.MediaPlayer;

// views have their own media player, thus this object is static.
public class AllelonMediaPlayer {
    private static MediaPlayer mediaPlayer;
    private static boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void startPlay(String url) {
        if (isPlaying()) {
            stopPlay();
        }

        try {
            playing = true;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void stopPlay() {
        playing = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
