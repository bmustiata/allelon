package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.player.AvailableStream;

public class MediaPlayerStatusEvent extends Event {
    public enum PlayerStatus {
        STOPPED,
        BUFFERING,
        PLAYING
    }

    public boolean playing;
    public int volume = 50;
    public PlayerStatus playerStatus = PlayerStatus.STOPPED;
    public AvailableStream playedStream = null; // this is set only when playerStatus != STOPPED
}
