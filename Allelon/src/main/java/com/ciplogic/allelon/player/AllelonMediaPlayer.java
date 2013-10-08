package com.ciplogic.allelon.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ciplogic.allelon.ToastProvider;
import com.ciplogic.allelon.player.proxy.StreamProxy;

import java.util.ArrayList;
import java.util.List;

// views have their own media player, thus this object is static.
public class AllelonMediaPlayer {
    private static MediaPlayer mediaPlayer;
    private static boolean playing;

    private StreamProxy proxy;

    private List<MediaPlayerListener> mediaPlayerListeners = new ArrayList<MediaPlayerListener>();

    private ToastProvider toastProvider;

    public AllelonMediaPlayer(ToastProvider toastProvider) {
        this.toastProvider = toastProvider;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void startPlay(String url) {
        if (isPlaying()) {
            stopPlay();
        }

        try {
            playing = true;

            proxy = new StreamProxy();
            proxy.init();
            proxy.start();

            url = String.format("http://127.0.0.1:%d/%s", proxy.getPort(), url);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            notifyStartPlaying();
        } catch (Exception e) {
            stopPlay(); // attempt to close the proxy if it was opened, and reset the playing flag.
            toastProvider.showToast("Unable to play stream.");
        }
    }

    public void stopPlay() {
        playing = false;
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } catch (Exception e) {
            // on purpose swallow it.
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
