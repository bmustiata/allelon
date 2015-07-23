package com.ciplogic.allelon.service;

import android.content.Context;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.MediaPlayerComponentStatusEvent;
import com.ciplogic.allelon.eventbus.events.MediaPlayerTitleEvent;
import com.ciplogic.allelon.notification.StreamNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Show a notification into the events bar of the Android,
 * with the status of the player, if it's playing, and what song
 * is playing.
 */
public class PlayingNotificationService implements EventListener {
    private final StreamNotification streamNotification;
    private boolean playing = false;

    public PlayingNotificationService(Context context) {
        this.streamNotification = new StreamNotification(context);
        EventBus.INSTANCE.registerListener(this);
    }

    /**
     * This will check for PREPARING/DESTROYING player component events, since they are
     * the most reliable in reporting the status of the player, and the MediaPlayerTitleEvent.
     *
     * This will update the status of the played song, only if the player is actually playing,
     * in order to avoid out of band title events that would just show the playing notification
     * when the player is not actually playing.
     *
     * @param event
     */
    @Override
    public void onEvent(Event event) {
        if (event instanceof MediaPlayerComponentStatusEvent) {
            MediaPlayerComponentStatusEvent mediaPlayerComponentStatus = (MediaPlayerComponentStatusEvent)event;
            if (mediaPlayerComponentStatus.playerState == PlayerState.PREPARING) {
                streamNotification.showNotification("Playing...");
                playing = true;
            } else if (mediaPlayerComponentStatus.playerState == PlayerState.DESTROYING) {
                streamNotification.hideNotification();
                playing = false;
            }
        }

        if (event instanceof MediaPlayerTitleEvent) {
            if (!playing) {
                return;
            }

            MediaPlayerTitleEvent mediaPlayerTitleEvent = (MediaPlayerTitleEvent)event;
            streamNotification.showNotification(mediaPlayerTitleEvent.title);
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        List result = new ArrayList();

        result.add(MediaPlayerTitleEvent.class);
        result.add(MediaPlayerComponentStatusEvent.class);

        return result;
    }
}
