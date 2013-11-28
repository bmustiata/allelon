package com.ciplogic.allelon.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ciplogic.allelon.MediaPlayerNotificationListener;
import com.ciplogic.allelon.RadioActivity;
import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AllelonMediaPlayer;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.songname.CurrentSongNameChangeListener;
import com.ciplogic.allelon.songname.CurrentSongNameProvider;

import java.util.ArrayList;
import java.util.List;

import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.BUFFERING;
import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.PLAYING;
import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.STOPPED;

public class ThreadMediaPlayer implements MediaPlayerListener, AMediaPlayer, CurrentSongNameChangeListener {
    private static ThreadMediaPlayer INSTANCE;

    private AMediaPlayer delegatePlayer;

    private volatile boolean playingThread = false;
    private volatile String url;

    private List<MediaPlayerListener> listenerList = new ArrayList<MediaPlayerListener>();

    private PlayerStatus playerStatus = STOPPED;

    private CurrentSongNameProvider currentSongNameProvider = new CurrentSongNameProvider();

    private ThreadMediaPlayer() {
        delegatePlayer = new AllelonMediaPlayer();
        delegatePlayer.addPlayerListener(this);
        this.addPlayerListener(new MediaPlayerNotificationListener());
        currentSongNameProvider.setCurrentSongNameChangeListener(this);
    }

    public static ThreadMediaPlayer getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ThreadMediaPlayer();
        }

        return INSTANCE;
    }

    @Override
    public synchronized boolean isPlaying() {
        return playingThread;
    }

    @Override
    public String getPlayedUrl() {
        return url;
    }

    @Override
    public synchronized void startPlay(String url) {
        if (isPlaying()) {
            if (isAlreadyPlayingUrl(url)) {
                return;
            }
            shutdownPlayer(true);
        }


        playingThread = true; // we mark the thread as playing, since we're going to start it anyway
        setUrl(url);

        notifyStartPlaying();
        changeStateToBuffering();
        notifyAll();

        Intent service = new Intent(RadioActivity.INSTANCE, MediaPlayerIntent.class);
        service.setData(Uri.parse(url));
        RadioActivity.INSTANCE.startService(service);
    }

    private void setUrl(String url) {
        this.url = url;
        currentSongNameProvider.setUrl(url);
    }

    private boolean isAlreadyPlayingUrl(String url) {
        return url.equals(this.url);
    }

    private void notifyStartPlaying() {
        for (MediaPlayerListener listener : listenerList) {
            listener.onStartStreaming();
        }
    }

    @Override
    public void stopPlay() {
        shutdownPlayer(true);
    }

    private void shutdownPlayer(boolean shouldWait) {
        if (url != null) { // if is already stopping, avoid deadlock via notify from observers.
            synchronized (this) {
                if(url != null) {
                    setUrl(null);
                    notifyAll();
                }

                if (shouldWait) {
                    waitRunningThreadToShutdown();
                }
            }
        }
    }

    private void waitRunningThreadToShutdown() {
        // as long as the thread is still playing I can't start yet doing anything, so wait for it.
        while (playingThread) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * This simply keeps the thread alive.
     */
    public void run(String givenUrl) {
        Log.d("Allelon", "Playing stream via intent: " + givenUrl);

        if (this.url == null) { // we were started from the intent directly, there is no activity running.
            setUrl(givenUrl);
            this.playingThread = true;
        }

        try {
            delegatePlayer.startPlay(url);
            changeStateToBuffering();

            int count = 0;
            int currentSecond, lastPlayedSecond = -1;

            synchronized (this) {
                // while we still have an URL to play, and we're not changing the stream.
                while (url != null) {
                    try {
                        wait(400);
                        count++;

                        if (count % 3 == 0) {
                            currentSecond = delegatePlayer.getCurrentSecond();
                            if (currentSecond <= lastPlayedSecond) {
                                changeStateToBuffering();
                            } else {
                                lastPlayedSecond = currentSecond;
                                changeStateToPlaying();
                            }
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }

            delegatePlayer.stopPlay();
            changeStateToStopped();
        } catch (Exception e) {
            Log.e("Allelon", e.getMessage(), e);
        } finally {
            synchronized (this) {
                playingThread = false;
                notifyStopPlaying();
                notifyAll();
            }
        }
    }

    private void changeStateToPlaying() {
        if (playerStatus != PLAYING) {
            playerStatus = PLAYING;
            notifyStatusChange(playerStatus);
        }
    }

    private void changeStateToBuffering() {
        if (playerStatus != BUFFERING) {
            playerStatus = BUFFERING;
            notifyStatusChange(playerStatus);
        }
    }

    private void changeStateToStopped() {
        if (playerStatus != STOPPED) {
            playerStatus = STOPPED;
            notifyStatusChange(playerStatus);
        }
    }

    private void notifyStopPlaying() {
        for (MediaPlayerListener listener : listenerList) {
            listener.onStopStreaming();
        }
    }

    @Override
    public synchronized void addPlayerListener(MediaPlayerListener listener) {
        listenerList.add(listener);
    }

    @Override
    public synchronized void removePlayerListener(MediaPlayerListener listener) {
        listenerList.remove(listener);
    }

    @Override
    public int getCurrentSecond() {
        return delegatePlayer.getCurrentSecond();
    }

    @Override
    public String getCurrentTitle() {
        return currentSongNameProvider.getCurrentTitle();
    }

    @Override
    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    @Override
    public void setVolume(int volume) {
        delegatePlayer.setVolume(volume);
    }

    @Override
    public int getVolume() {
        return delegatePlayer.getVolume();
    }

    @Override
    public void onStatusChange(PlayerStatus playerStatus) {
    }

    private void notifyStatusChange(PlayerStatus playerStatus) {
        for (MediaPlayerListener listener : listenerList) {
            listener.onStatusChange(playerStatus);
        }
    }

    @Override
    public void onStartStreaming() {
    }

    @Override
    public void onStopStreaming() {
        shutdownPlayer(false);
    }

    @Override
    public void onTitleChange(String title) {
        notifyStatusChange(playerStatus); // simply to update the UI, probably a more specific update would make sense.
    }
}
