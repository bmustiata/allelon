package com.ciplogic.allelon.player;

import android.media.AudioManager;
import android.util.Log;

import com.ciplogic.allelon.ToastProvider;

import java.util.ArrayList;
import java.util.List;

public class AllelonMediaPlayer implements AMediaPlayer {
    private VolumeMediaPlayer mediaPlayer;
    private String playedUrl;

    private List<MediaPlayerListener> mediaPlayerListeners = new ArrayList<MediaPlayerListener>();

    private ToastProvider toastProvider;

    private final static String LOG_TAG = AllelonMediaPlayer.class.getName();

    private int volume = 100; // keep the volume as well

    public AllelonMediaPlayer() {
        this.toastProvider = new ToastProvider();
    }

    @Override
    public boolean isPlaying() {
        return playedUrl != null;
    }

    @Override
    public String getPlayedUrl() {
        return playedUrl;
    }

    @Override
    public void startPlay(String url) {
        if (isPlaying()) {
            if (isSameStreamAlreadyPlaying(url)) {
                return;
            }

            stopPlay();
        }

        try {
            playedUrl = url;

            mediaPlayer = new VolumeMediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setVolume(volume);
            mediaPlayer.prepare();
            mediaPlayer.start();

            notifyStartPlaying();
        } catch (Exception e) {
            Log.e("Allelon", e.getMessage(), e);
            errorWithTheStream();
        }
    }

    private void errorWithTheStream() {
        toastProvider.showToast("Unable to play stream.");
        stopPlay(); // attempt to close the proxy if it was opened, and reset the playing flag.
    }

    private boolean isSameStreamAlreadyPlaying(String url) {
        return playedUrl.equals(url);
    }

    @Override
    public void stopPlay() {
        playedUrl = null;

        try {
            if (mediaPlayer != null) {
                closeMediaPlayer();
                notifyStopPlaying();
            }
        } catch (Exception e) {
            // on purpose swallow it.
        }
    }

    private void closeMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
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

    @Override
    public void addPlayerListener(MediaPlayerListener listener) {
        mediaPlayerListeners.add(listener);
    }

    @Override
    public void removePlayerListener(MediaPlayerListener listener) {
        mediaPlayerListeners.remove(listener);
    }

    @Override
    public int getCurrentSecond() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public String getCurrentTitle() {
        throw new IllegalStateException("Not implemented. The current title is not derived from the stream.");
    }

    @Override
    public MediaPlayerListener.PlayerStatus getPlayerStatus() {
        throw new IllegalStateException("Not implemented. Some listeners should be set on the mediaPlayer if really needed.");
    }

    @Override
    public void setVolume(int volume) {
        this.volume = volume;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    @Override
    public int getVolume() {
        return volume;
    }
}
