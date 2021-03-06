package com.ciplogic.allelon.eventbus;

import android.content.Context;

import com.ciplogic.allelon.news.NewsProvider;
import com.ciplogic.allelon.service.MediaPlayerComponent;
import com.ciplogic.allelon.service.PlayingNotificationService;
import com.ciplogic.allelon.service.WakeLockService;
import com.ciplogic.allelon.songname.CurrentSongNameProvider;

/**
 * Ensures that the required objects are created and registered
 * to the EventBus.
 */
public class ObjectInstantiator {
    private static MediaPlayerComponent mediaPlayerComponent;
    private static CurrentSongNameProvider currentSongNameProvider;
    private static WakeLockService wakeLockService;
    private static PlayingNotificationService playingNotificationService;
    private static NewsProvider newsProvider;

    public static void ensureInstantiated(Context context) {
        if (mediaPlayerComponent != null) {
            return;
        }

        mediaPlayerComponent = new MediaPlayerComponent();
        currentSongNameProvider = new CurrentSongNameProvider();
        wakeLockService = new WakeLockService(context);
        playingNotificationService = new PlayingNotificationService(context);
        newsProvider = new NewsProvider();
    }
}
