package com.ciplogic.allelon;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.MediaPlayerListener;

public class MediaPlayerNotificationListener implements MediaPlayerListener {
    private final StreamNotification streamNotification;

    public MediaPlayerNotificationListener() {
        this.streamNotification = new StreamNotification();
    }

    @Override
    public void onStatusChange(PlayerStatusChangeEvent playerStatus) {
        switch (playerStatus.playerStatus) {
            case PLAYING:
                // if we have a song title, we use that as the status
                if (playerStatus.song != null && !playerStatus.song.matches("^\\s*$")) {
                    streamNotification.showNotification( playerStatus.song );
                } else {
                    streamNotification.showNotification("Playing...");
                }

                break;
            case BUFFERING:
                streamNotification.showNotification("Buffering...");
                break;
            case STOPPED:
                streamNotification.hideNotification();
                break;
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
