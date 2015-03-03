package com.ciplogic.allelon.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import com.ciplogic.allelon.MediaPlayerNotificationListener;
import com.ciplogic.allelon.PlayActivity;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.player.VolumeMediaPlayer;
import com.ciplogic.allelon.songname.CurrentSongNameChangeListener;
import com.ciplogic.allelon.songname.CurrentSongNameProvider;

import java.util.ArrayList;
import java.util.List;

import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.BUFFERING;
import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.PLAYING;
import static com.ciplogic.allelon.player.MediaPlayerListener.PlayerStatus.STOPPED;

public class ThreadMediaPlayer implements MediaPlayerListener, CurrentSongNameChangeListener {
    private static ThreadMediaPlayer INSTANCE;

    private volatile boolean playingThread = false;
    private volatile String url;

    private List<MediaPlayerListener> listenerList = new ArrayList<MediaPlayerListener>();

    private PlayerStatus playerStatus = STOPPED;

    private CurrentSongNameProvider currentSongNameProvider = new CurrentSongNameProvider();

    private int volume = 100;

    private ThreadMediaPlayer() {
        this.addPlayerListener(new MediaPlayerNotificationListener());
        currentSongNameProvider.setCurrentSongNameChangeListener(this);
    }

    public static ThreadMediaPlayer getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ThreadMediaPlayer();
        }

        return INSTANCE;
    }

    public synchronized boolean isPlaying() {
        return playingThread;
    }

    public String getPlayedUrl() {
        return url;
    }

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

        Intent service = new Intent(PlayActivity.INSTANCE, MediaPlayerIntent.class);
        service.setData(Uri.parse(url));
        PlayActivity.INSTANCE.startService(service);
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
            int count = 0;
            int currentSecond, lastPlayedSecond = -1;

            synchronized (this) {
                VolumeMediaPlayer mediaPlayer = null;

                try {
                    mediaPlayer = new VolumeMediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.setVolume(volume);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    changeStateToBuffering();

                    // while we still have an URL to play, and we're not changing the stream.
                    while (url != null) {
                        try {
                            wait(200);
                            count++;

                            mediaPlayer.setVolume(volume); // FIXME

                            if (count % 3 == 0) {
                                currentSecond = mediaPlayer.getCurrentPosition();
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

                } finally {
                    mediaPlayer.stop();
                }

                changeStateToStopped();
                playingThread = false;
                notifyAll();
            }
            notifyStopPlaying();
        } catch (Exception e) {
            Log.e("Allelon", e.getMessage(), e);
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

    public synchronized void addPlayerListener(MediaPlayerListener listener) {
        listenerList.add(listener);
    }

    public synchronized void removePlayerListener(MediaPlayerListener listener) {
        listenerList.remove(listener);
    }

    public String getCurrentTitle() {
        return currentSongNameProvider.getCurrentTitle();
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
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
