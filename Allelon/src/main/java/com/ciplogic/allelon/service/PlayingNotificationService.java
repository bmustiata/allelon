package com.ciplogic.allelon.service;

import android.content.Context;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.MediaPlayerComponentStatusEvent;
import com.ciplogic.allelon.notification.StreamNotification;

import java.util.Collections;
import java.util.List;

/**
 * Show a notification into the events bar of the Android,
 * with the status of the player, if it's playing, and what song
 * is playing.
 */
public class PlayingNotificationService implements EventListener {
    private final StreamNotification streamNotification;

    public PlayingNotificationService(Context context) {
        this.streamNotification = new StreamNotification(context);
        EventBus.INSTANCE.registerListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MediaPlayerComponentStatusEvent) {
            MediaPlayerComponentStatusEvent mediaPlayerComponentStatus = (MediaPlayerComponentStatusEvent)event;
            if (mediaPlayerComponentStatus.playerState == PlayerState.PREPARING) {
                streamNotification.showNotification("Playing...");
            } else if (mediaPlayerComponentStatus.playerState == PlayerState.DESTROYING) {
                streamNotification.hideNotification();
            }
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        return (List) Collections.singletonList(MediaPlayerComponentStatusEvent.class);
    }
}
