package com.ciplogic.allelon.service;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ciplogic.allelon.player.VolumeMediaPlayer;

import java.io.IOException;

public class MediaPlayerHolder implements MediaPlayer.OnPreparedListener {
    private VolumeMediaPlayer mediaPlayer;
    private MediaPlayerComponent mediaPlayerComponent;

    private volatile boolean isDestroyed;
    private volatile boolean isPreparing = true;

    public MediaPlayerHolder(MediaPlayerComponent mediaPlayerComponent, int volume) {
        this.mediaPlayerComponent = mediaPlayerComponent;

        System.out.println("new holder: " + this);
        mediaPlayer = new VolumeMediaPlayer(volume);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        System.out.println("prepared: " + this);
        synchronized (this) {
            if (isDestroyed) {
                releaseMediaPlayer();
                System.out.println("prepared destroyed: " + this);

                return;
            }

            isPreparing = false;
        }

        System.out.println("calling on prepared on component " + this);
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
        System.out.println("prepare async " + this);
        mediaPlayer.prepareAsync();
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {
        System.out.println("stopping: " + this);
        synchronized (this) {
            isDestroyed = true;
        }
        System.out.println("stop: " + this);

        if (isPreparing) {
            return;
        }

        releaseMediaPlayer();
    }

    public void setVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }
}
