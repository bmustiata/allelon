package com.ciplogic.allelon;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.MediaPlayerListener;

public class MediaPlayerNotificationListener implements MediaPlayerListener {
    private final StreamNotification streamNotification;

    public MediaPlayerNotificationListener() {
        this.streamNotification = new StreamNotification();
    }

    @Override
    public void onStatusChange(PlayerStatus playerStatus) {
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
