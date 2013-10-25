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
        switch (playerStatus) {
            case PLAYING: streamNotification.showNotification("Playing..."); break;
            case BUFFERING: streamNotification.showNotification("Buffering..."); break;
            case STOPPED: streamNotification.hideNotification(); break;
        }
    }

    @Override
    public void onStartStreaming() {
    }

    @Override
    public void onStopStreaming() {
        streamNotification.hideNotification();
    }
}
