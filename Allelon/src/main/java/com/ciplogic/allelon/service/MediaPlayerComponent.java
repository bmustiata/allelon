package com.ciplogic.allelon.service;

import android.media.MediaPlayer;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.uiactions.ChangeVolumeEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.SelectStreamEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StartPlayEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StopPlayEvent;
import com.ciplogic.allelon.player.AvailableStream;

import java.util.ArrayList;
import java.util.List;

/**
 * This simply listens the event bus, and creates or destroys the media player on demand.
 */
public class MediaPlayerComponent implements EventListener {
    PlayerState playerState = PlayerState.STOPPED;

    // media players can't have their URL changed without being restarted.
    private MediaPlayerHolder mediaPlayerHolder;
    private int volume = 100;

    // in case the user doesn't selects andything, pick the first
    private AvailableStream selectedStream = AvailableStream.values()[0];

    public MediaPlayerComponent() {
        EventBus.INSTANCE.registerListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof StartPlayEvent) {
            System.out.println("[start]");
            handleStartPlayEvent((StartPlayEvent) event);
        } else if (event instanceof StopPlayEvent) {
            handleStopPlayEvent();
        } else if (event instanceof SelectStreamEvent) {
            System.out.println("[change]");
            handleSelectStreamEvent((SelectStreamEvent) event);
        } else if (event instanceof ChangeVolumeEvent) {
            handleChangeVolumeEvent((ChangeVolumeEvent) event);
        }
    }

    private void handleChangeVolumeEvent(ChangeVolumeEvent event) {
        volume = event.value;

        if (playerState != PlayerState.PREPARING && playerState != PlayerState.PLAYING) {
            return;
        }

        mediaPlayerHolder.setVolume(volume);
    }

    private void handleSelectStreamEvent(SelectStreamEvent event) {
        this.selectedStream = event.availableStream;

        if (playerState != PlayerState.PREPARING && playerState != PlayerState.PLAYING) {
            return;
        }

        handleStopPlayEvent();
        handleStartPlayEvent(null);
    }

    private void handleStopPlayEvent() {
        if (playerState != PlayerState.PREPARING && playerState != PlayerState.PLAYING) {
            throw new IllegalStateException("Invalid state: " + playerState);
        }

        changeState(PlayerState.DESTROYING);
        shutDownMediaPlayer(mediaPlayerHolder);

        changeState(PlayerState.STOPPED);
    }

    private void shutDownMediaPlayer(MediaPlayerHolder mediaPlayer) {
        System.out.println("shutdown media player: " + mediaPlayer);
        this.mediaPlayerHolder = null;
        mediaPlayer.stop();
    }

    private void handleStartPlayEvent(StartPlayEvent event) {
        try {
            if (playerState != PlayerState.STOPPED) {
                throw new IllegalStateException("Invalid state: " + playerState);
            }

            if (mediaPlayerHolder != null) {
                throw new IllegalStateException("what?");
            }

            changeState(PlayerState.PREPARING);

            mediaPlayerHolder = new MediaPlayerHolder(this, volume);
            mediaPlayerHolder.setDataSource(selectedStream.getUrl());

            mediaPlayerHolder.prepareAsync();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create player:" + e.getMessage(), e);
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        List<Class<? extends Event>> result = new ArrayList<Class<? extends Event>>();

        result.add(StartPlayEvent.class);
        result.add(StopPlayEvent.class);
        result.add(ChangeVolumeEvent.class);
        result.add(SelectStreamEvent.class);

        return result;
    }

    public void onPrepared(MediaPlayerHolder mediaPlayerHolder) {
        if (mediaPlayerHolder != this.mediaPlayerHolder) {
            throw new IllegalStateException("What?");
        }

        if (playerState != PlayerState.PREPARING) {
            throw new IllegalStateException("Player state is " + playerState);
        }

        mediaPlayerHolder.start();

        changeState(PlayerState.PLAYING);
    }

    private void changeState(PlayerState playerState) {
        System.out.println("Old state: " + this.playerState + " new: " + playerState);
        this.playerState = playerState;
    }
}
