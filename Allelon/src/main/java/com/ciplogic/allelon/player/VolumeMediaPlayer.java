package com.ciplogic.allelon.player;

import android.media.MediaPlayer;

/**
 * A media player that has a volume property.
 */
public class VolumeMediaPlayer extends MediaPlayer {
    private int volume = 100; // volume is from 0, to 100.

    public VolumeMediaPlayer(int volume) {
        setVolume(volume);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        float androidVolume = volume / 100.0f;
        this.volume = volume;

        this.setVolume(androidVolume, androidVolume);
    }
}
