package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.player.AvailableStream;

/**
 * Event that will be triggered on demand as the result of a
 * RequestMediaPlayerStatusEvent that is fired by the UI, when it wants
 * to initialize the Activity, and update its views. This is not representing
 * the internal state of the of the MediaPlayerComponent.
 */
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
