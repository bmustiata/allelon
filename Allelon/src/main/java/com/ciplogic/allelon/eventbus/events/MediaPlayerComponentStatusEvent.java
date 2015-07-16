package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.service.PlayerState;

/**
 * This event is fired by the MediaPlayerComponent to show its exact
 * state it's in. This should be used by services that need the
 * exact status of the player.
 *
 * Currently this is only the WakeLockService.
 */
public class MediaPlayerComponentStatusEvent extends Event {
    public final PlayerState playerState;

    public MediaPlayerComponentStatusEvent(PlayerState playerState) {
        this.playerState = playerState;
    }
}
