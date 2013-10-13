package com.ciplogic.allelon.service;

import android.content.Context;

import com.ciplogic.allelon.MediaPlayerNotificationListener;
import com.ciplogic.allelon.ToastProvider;
import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AllelonMediaPlayer;
import com.ciplogic.allelon.player.MediaPlayerListener;

public class ThreadMediaPlayer implements MediaPlayerListener, AMediaPlayer, Runnable {
    private static ThreadMediaPlayer INSTANCE;

    private AMediaPlayer delegatePlayer;

    private volatile boolean playingDelegate = false;

    private ThreadMediaPlayer(ToastProvider toastProvider,
                              MediaPlayerNotificationListener mediaPlayerNotificationListener) {
        delegatePlayer = new AllelonMediaPlayer(toastProvider);
        delegatePlayer.addPlayerListener(this);
        delegatePlayer.addPlayerListener(mediaPlayerNotificationListener);
    }

    public static ThreadMediaPlayer getInstance(Context context) {
        if (INSTANCE == null) {
            StreamNotification streamNotification = new StreamNotification(context);
            MediaPlayerNotificationListener mediaPlayerNotificationListener = new MediaPlayerNotificationListener(streamNotification);
            ToastProvider toastProvider = new ToastProvider(context);

            INSTANCE = new ThreadMediaPlayer(toastProvider, mediaPlayerNotificationListener);
        }

        return INSTANCE;
    }

    @Override
    public synchronized boolean isPlaying() {
        return delegatePlayer.isPlaying();
    }

    @Override
    public synchronized void startPlay(String url) {
        delegatePlayer.startPlay(url);
        Thread daemonThread = new Thread(INSTANCE);
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    @Override
    public synchronized void stopPlay() {
        delegatePlayer.stopPlay();
    }

    /**
     * This simply keeps the thread alive.
     */
    @Override
    public void run() {
        while (playingDelegate) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                }
            }
        }
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
    public synchronized void onStartStreaming() {
        playingDelegate = true;
        this.notify();
    }

    @Override
    public synchronized void onStopStreaming() {
        playingDelegate = false;
        this.notify();
    }
}
