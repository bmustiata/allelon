package com.ciplogic.allelon.player;

public interface MediaPlayerListener {
    enum PlayerStatus {
        STOPPED,
        BUFFERING,
        PLAYING
    }

    class PlayerStatusChangeEvent {
        public PlayerStatus playerStatus;
        public String song = "";

        public PlayerStatusChangeEvent(PlayerStatus playerStatus) {
            this.playerStatus = playerStatus;
        }

        public PlayerStatusChangeEvent(PlayerStatus playerStatus, String song) {
            this.playerStatus = playerStatus;
            this.song = song;
        }
    }

    void onStatusChange(PlayerStatusChangeEvent playerStatus);

    void onStartStreaming();
    void onStopStreaming();
}
