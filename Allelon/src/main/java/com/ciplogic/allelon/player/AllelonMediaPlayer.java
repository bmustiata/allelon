package com.ciplogic.allelon.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ciplogic.allelon.player.proxy.StreamProxy;

import java.util.ArrayList;
import java.util.List;

// views have their own media player, thus this object is static.
public class AllelonMediaPlayer {
    private static MediaPlayer mediaPlayer;
    private static boolean playing;

    private StreamProxy proxy;

    private List<MediaPlayerListener> mediaPlayerListeners = new ArrayList<MediaPlayerListener>();

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

            notifyStartPlaying();
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
        } finally { // we definitelly want to kill the proxy, even if the media player crashes.
            if (proxy != null) {
                proxy.stop();
            }
        }

        notifyStopPlaying();
    }

    private void notifyStartPlaying() {
        for (MediaPlayerListener listener : mediaPlayerListeners) {
            listener.onStartStreaming();
        }
    }

    private void notifyStopPlaying() {
        for (MediaPlayerListener listener : mediaPlayerListeners) {
            listener.onStopStreaming();
        }
    }

    public void addPlayerListener(MediaPlayerListener listener) {
        mediaPlayerListeners.add(listener);
    }

    public void removePlayerListener(MediaPlayerListener listener) {
        mediaPlayerListeners.remove(listener);
    }
}
