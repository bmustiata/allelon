package com.ciplogic.allelon.player;

public interface MediaPlayerListener {
    public enum PlayerStatus {
        STOPPED,
        BUFFERING,
        PLAYING
    }

    void onStatusChange(PlayerStatus playerStatus);

    void onStartStreaming();
    void onStopStreaming();
}
