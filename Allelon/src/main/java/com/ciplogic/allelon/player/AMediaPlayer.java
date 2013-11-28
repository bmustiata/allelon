package com.ciplogic.allelon.player;

public interface AMediaPlayer {
    boolean isPlaying();

    String getPlayedUrl();

    void startPlay(String url);
    void stopPlay();

    void addPlayerListener(MediaPlayerListener listener);
    void removePlayerListener(MediaPlayerListener listener);

    int getCurrentSecond();

    String getCurrentTitle();

    MediaPlayerListener.PlayerStatus getPlayerStatus();

    void setVolume(int i);
    int getVolume();
}
