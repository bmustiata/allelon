package com.ciplogic.allelon.eventbus;

import android.content.Context;

import com.ciplogic.allelon.service.MediaPlayerComponent;

/**
 * Ensures that the required objects are created and registered
 * to the EventBus.
 */
public class ObjectInstantiator {
    private static MediaPlayerComponent mediaPlayerComponent;

    public static void ensureInstantiated(Context context) {
        if (mediaPlayerComponent != null) {
            return;
        }

        mediaPlayerComponent = new MediaPlayerComponent();
    }
}
