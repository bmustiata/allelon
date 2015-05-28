package com.ciplogic.allelon.service;

import android.app.IntentService;
import android.content.Intent;

import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.ObjectInstantiator;
import com.ciplogic.allelon.eventbus.events.uiactions.SelectStreamEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StartPlayEvent;
import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AvailableStream;

/**
 * Starts the player via an intent, to consume fewer resources instead
 * of the full resources for the UI.
 */
public class MediaPlayerIntent extends IntentService {
    public MediaPlayerIntent() {
        super("service.MediaPlayerIntent");
        ObjectInstantiator.ensureInstantiated(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            startForeground(0, new StreamNotification().buildNotification("Starting service..."));
            String streamUrl = intent.getDataString();

            EventBus.INSTANCE.fire(new SelectStreamEvent(AvailableStream.fromUrl(streamUrl)));
            EventBus.INSTANCE.fire(new StartPlayEvent());
        } finally {
            stopForeground(true);
        }
    }

    @Override
    public void onDestroy() {
        // make sure the notification also goes down, if Android nukes our process.
        new StreamNotification().hideNotification();
        super.onDestroy();
    }
}
