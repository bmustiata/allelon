package com.ciplogic.allelon;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.MediaPlayerListener;

public class MediaPlayerNotificationListener implements MediaPlayerListener {
    private final StreamNotification streamNotification;

    public MediaPlayerNotificationListener(StreamNotification streamNotification) {
        this.streamNotification = streamNotification;
    }

    @Override
    public void onStartStreaming() {
        streamNotification.showNotification("Playing...");
    }

    @Override
    public void onStopStreaming() {
        streamNotification.hideNotification();
    }
}
