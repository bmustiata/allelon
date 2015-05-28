package com.ciplogic.allelon.eventbus;

import android.content.Context;

import com.ciplogic.allelon.service.MediaPlayerComponent;
import com.ciplogic.allelon.service.ThreadMediaPlayer;

/**
 * Ensures that the required objects are created and registered
 * to the EventBus.
 */
public class ObjectInstantiator {
    public static void ensureInstantiated(Context context) {
        //ThreadMediaPlayer threadMediaPlayer = ThreadMediaPlayer.getInstance(context);
        new MediaPlayerComponent();
    }
}
