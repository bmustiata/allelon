package com.ciplogic.allelon;

import android.media.AudioManager;
import android.media.MediaPlayer;

// views have their own media player, thus this object is static.
public class AllelonMediaPlayer {
    private static MediaPlayer mediaPlayer;
    private static boolean playing;

    private StreamProxy proxy;

    public boolean isPlaying() {
        return playing;
    }

    public void startPlay(String url) {
        if (isPlaying()) {
            stopPlay();
        }

        try {
            proxy = new StreamProxy();
            proxy.init();
            proxy.start();

            url = String.format("http://127.0.0.1:%d/%s", proxy.getPort(), url);

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
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } finally {
            proxy.stop();
        }
    }
}
