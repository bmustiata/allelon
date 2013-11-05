package com.ciplogic.allelon.service;

import android.app.IntentService;
import android.content.Intent;

import com.ciplogic.allelon.notification.StreamNotification;

public class MediaPlayerIntent extends IntentService {
    private ThreadMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    public MediaPlayerIntent() {
        super("service.MediaPlayerIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            startForeground(0, new StreamNotification().buildNotification("Starting service..."));
            allelonMediaPlayer.run(intent.getDataString());
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
