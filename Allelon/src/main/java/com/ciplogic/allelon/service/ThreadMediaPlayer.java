package com.ciplogic.allelon.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ciplogic.allelon.MediaPlayerNotificationListener;
import com.ciplogic.allelon.ToastProvider;
import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AllelonMediaPlayer;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.player.proxy.StreamConnectionListener;

public class ThreadMediaPlayer implements MediaPlayerListener, AMediaPlayer, StreamConnectionListener, Runnable {
    private static ThreadMediaPlayer INSTANCE;

    private final Context context;
    private final ToastProvider toastProvider;

    private AMediaPlayer delegatePlayer;

    private volatile boolean playingDelegate = false;
    private volatile String url;

    private ThreadMediaPlayer(ToastProvider toastProvider,
                              MediaPlayerNotificationListener mediaPlayerNotificationListener, Context context) {
        delegatePlayer = new AllelonMediaPlayer(toastProvider, this);
        delegatePlayer.addPlayerListener(this);
        delegatePlayer.addPlayerListener(mediaPlayerNotificationListener);
        this.context = context;
        this.toastProvider = toastProvider;
    }

    public static ThreadMediaPlayer getInstance(Context context) {
        if (INSTANCE == null) {
            StreamNotification streamNotification = new StreamNotification(context);
            MediaPlayerNotificationListener mediaPlayerNotificationListener = new MediaPlayerNotificationListener(streamNotification);
            ToastProvider toastProvider = new ToastProvider(context);

            INSTANCE = new ThreadMediaPlayer(toastProvider, mediaPlayerNotificationListener, context);
        }

        return INSTANCE;
    }

    @Override
    public synchronized boolean isPlaying() {
        return playingDelegate;
    }

    @Override
    public String getPlayedUrl() {
        return url;
    }

    @Override
    public synchronized void startPlay(String url) {
        playingDelegate = true;
        this.url = url;
        notifyAll();
        context.startService(new Intent(context, MediaPlayerIntent.class));
    }

    @Override
    public synchronized void stopPlay() {
        playingDelegate = false;
        url = null;
        notifyAll();
    }

    /**
     * This simply keeps the thread alive.
     */
    @Override
    public void run() {
        delegatePlayer.startPlay(url);
        while (playingDelegate) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                }
            }
        }
        delegatePlayer.stopPlay();
    }

    @Override
    public synchronized void addPlayerListener(MediaPlayerListener listener) {
        delegatePlayer.addPlayerListener(listener);
    }

    @Override
    public synchronized void removePlayerListener(MediaPlayerListener listener) {
        delegatePlayer.removePlayerListener(listener);
    }

    @Override
    public void onStartStreaming() {
    }

    @Override
    public void onStopStreaming() {
        stopPlay();
    }

    @Override
    public void onStreamClosed() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toastProvider.showToast("Stream connection closed. Allelon will exit.");
            }
        });

        stopPlay();
    }
}
