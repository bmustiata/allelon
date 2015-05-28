package com.ciplogic.allelon.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import com.ciplogic.allelon.PlayActivity;
import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.uiactions.ChangeVolumeEvent;
import com.ciplogic.allelon.eventbus.events.MediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.RequestMediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.SelectStreamEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StartPlayEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StopPlayEvent;
import com.ciplogic.allelon.player.AvailableStream;
import com.ciplogic.allelon.player.VolumeMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Plays the music
 */
public class ThreadMediaPlayer implements EventListener {
    private static ThreadMediaPlayer INSTANCE;

    // media players can't have their URL changed without being restarted.
    private VolumeMediaPlayer mediaPlayer;
    private int volume = 100;

    private AvailableStream selectedStream = AvailableStream.values()[0];

    private volatile boolean playingThread = false;
    private volatile String url;

    private MediaPlayerStatusEvent.PlayerStatus playerStatus = MediaPlayerStatusEvent.PlayerStatus.STOPPED;

    private ThreadMediaPlayer() {
        EventBus.INSTANCE.registerListener(this);
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

        this.playingThread = true; // we mark the thread as playing, since we're going to start it anyway
        this.url = url;

        changeStateToBuffering();
        notifyAll();

        Intent service = new Intent(PlayActivity.INSTANCE, MediaPlayerIntent.class);
        service.setData(Uri.parse(url));
        PlayActivity.INSTANCE.startService(service);
    }

    private boolean isAlreadyPlayingUrl(String url) {
        return isPlaying() && url.equals(this.url);
    }

    public void stopPlay() {
        shutdownPlayer(true);
    }

    private synchronized void shutdownPlayer(boolean shouldWait) {
        if (url != null) { // if is already stopping, avoid deadlock via notify from observers.
            url = null;
            notifyAll();

            if (shouldWait) {
                waitRunningThreadToShutdown();
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
            this.url = givenUrl;
            this.playingThread = true;
        }

        try {
            int count = 0;
            int currentSecond, lastPlayedSecond = -1;

            synchronized (this) {
                try {
                    mediaPlayer = new VolumeMediaPlayer(volume);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    changeStateToBuffering();

                    // while we still have an URL to play, and we're not changing the stream.
                    while (url != null) {
                        try {
                            wait(200);
                            count++;

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
        } catch (Exception e) {
            Log.e("Allelon", e.getMessage(), e);
        }
    }

    private void changeStateToPlaying() {
        if (playerStatus != MediaPlayerStatusEvent.PlayerStatus.PLAYING) {
            playerStatus = MediaPlayerStatusEvent.PlayerStatus.PLAYING;
            notifyPlayerStatus(playerStatus);
        }
    }

    private void changeStateToBuffering() {
        if (playerStatus != MediaPlayerStatusEvent.PlayerStatus.BUFFERING) {
            playerStatus = MediaPlayerStatusEvent.PlayerStatus.BUFFERING;
            notifyPlayerStatus(playerStatus);
        }
    }

    private void changeStateToStopped() {
        if (playerStatus != MediaPlayerStatusEvent.PlayerStatus.STOPPED) {
            playerStatus = MediaPlayerStatusEvent.PlayerStatus.STOPPED;
            notifyPlayerStatus(playerStatus);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ChangeVolumeEvent) {
            ChangeVolumeEvent changeVolumeEvent = (ChangeVolumeEvent) event;
            this.setVolume(changeVolumeEvent.value);
        } else if (event instanceof RequestMediaPlayerStatusEvent) {
            notifyPlayerStatus(playerStatus);
        } else if (event instanceof StartPlayEvent) {
            startPlay(selectedStream.getUrl());
        } else if (event instanceof StopPlayEvent) {
            stopPlay();
        } else if (event instanceof SelectStreamEvent) {
            SelectStreamEvent selectStreamEvent = (SelectStreamEvent) event;
            selectedStream = selectStreamEvent.availableStream;
            if (isPlaying()) {
                startPlay(selectedStream.getUrl());
            }
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        List<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>();

        events.add(ChangeVolumeEvent.class);
        events.add(RequestMediaPlayerStatusEvent.class);
        events.add(StartPlayEvent.class);
        events.add(StopPlayEvent.class);
        events.add(SelectStreamEvent.class);

        return events;
    }

    private void notifyPlayerStatus(MediaPlayerStatusEvent.PlayerStatus playerStatus) {
        MediaPlayerStatusEvent event = new MediaPlayerStatusEvent();

        event.playerStatus = playerStatus;
        event.playing = playingThread;
        event.volume = volume;
        if (url != null) {
            event.playedStream = AvailableStream.fromUrl(url);
        }

        EventBus.INSTANCE.fire(event);
    }

    public void setVolume(int volume) {
        this.volume = volume;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }
}
