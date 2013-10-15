package com.ciplogic.allelon.service;

import android.app.IntentService;
import android.content.Intent;

public class MediaPlayerIntent extends IntentService {
    private ThreadMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    public MediaPlayerIntent() {
        super("service.MediaPlayerIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        allelonMediaPlayer.run();
    }
}
