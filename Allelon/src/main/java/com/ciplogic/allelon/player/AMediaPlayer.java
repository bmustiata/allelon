package com.ciplogic.allelon.player;

public interface AMediaPlayer {
    boolean isPlaying();
    void startPlay(String url);
    void stopPlay();

    void addPlayerListener(MediaPlayerListener listener);

    void removePlayerListener(MediaPlayerListener listener);
}
