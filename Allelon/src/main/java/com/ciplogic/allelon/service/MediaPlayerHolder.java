package com.ciplogic.allelon.service;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ciplogic.allelon.player.VolumeMediaPlayer;

import java.io.IOException;

/**
 * This exists because in the Android code exists a race condition, where
 * <code>onPrepared()</code> will be called even if <code>stop()</code> was
 * already called on the media player.
 */
public class MediaPlayerHolder implements MediaPlayer.OnPreparedListener {
    private VolumeMediaPlayer mediaPlayer;
    private MediaPlayerComponent mediaPlayerComponent;

    private volatile boolean isDestroyed;
    private volatile boolean isPreparing = true;

    public MediaPlayerHolder(MediaPlayerComponent mediaPlayerComponent, int volume) {
        this.mediaPlayerComponent = mediaPlayerComponent;

        mediaPlayer = new VolumeMediaPlayer(volume);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        synchronized (this) {
            if (isDestroyed) {
                releaseMediaPlayer();

                return;
            }

            isPreparing = false;
        }

        mediaPlayerComponent.onPrepared(this);
    }

    private void releaseMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void setDataSource(String dataSource) {
        try {
            mediaPlayer.setDataSource(dataSource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to set data source: " + dataSource + ", " + e.getMessage(), e);
        }
    }

    public void prepareAsync() {
        mediaPlayer.prepareAsync();
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {
        synchronized (this) {
            isDestroyed = true;
        }

        if (isPreparing) {
            return;
        }

        releaseMediaPlayer();
    }

    public void setVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }
}
