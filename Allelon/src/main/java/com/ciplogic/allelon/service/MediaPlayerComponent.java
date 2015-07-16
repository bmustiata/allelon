package com.ciplogic.allelon.service;

import android.telephony.TelephonyManager;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.MediaPlayerComponentStatusEvent;
import com.ciplogic.allelon.eventbus.events.MediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.PhoneCallStatusEvent;
import com.ciplogic.allelon.eventbus.events.RequestMediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.ChangeVolumeEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.SelectStreamEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StartPlayEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StopPlayEvent;
import com.ciplogic.allelon.player.AvailableStream;

import java.util.ArrayList;
import java.util.List;

/**
 * This simply listens the event bus, and creates or destroys the media player on demand.
 * It fires events on the bus with the current player state of the component, when it is
 * being requested.
 */
public class MediaPlayerComponent implements EventListener {
    private PlayerState playerState = PlayerState.STOPPED;

    // media players can't have their URL changed without being restarted.
    private MediaPlayerHolder mediaPlayerHolder;
    private int volume = 100;

    // in case the user doesn't selects anything, pick the first
    private AvailableStream selectedStream = AvailableStream.values()[0];

    public MediaPlayerComponent() {
        EventBus.INSTANCE.registerListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof StartPlayEvent) {
            handleStartPlayEvent((StartPlayEvent) event);
        } else if (event instanceof StopPlayEvent) {
            handleStopPlayEvent();
        } else if (event instanceof SelectStreamEvent) {
            handleSelectStreamEvent((SelectStreamEvent) event);
        } else if (event instanceof ChangeVolumeEvent) {
            handleChangeVolumeEvent((ChangeVolumeEvent) event);
        } else if (event instanceof PhoneCallStatusEvent) {
            handlePhoneCallEvent((PhoneCallStatusEvent)event);
        } else if (event instanceof RequestMediaPlayerStatusEvent) {
            notifyPlayerStatusChange((RequestMediaPlayerStatusEvent) event);
        }
    }

    private void notifyPlayerStatusChange(RequestMediaPlayerStatusEvent event) {
        MediaPlayerStatusEvent mediaPlayerStatus = new MediaPlayerStatusEvent();

        mediaPlayerStatus.volume = volume;
        mediaPlayerStatus.playing = playerState == PlayerState.PREPARING ||
                playerState == PlayerState.PLAYING;
        mediaPlayerStatus.playerStatus = mediaPlayerStatus.playing ?
                MediaPlayerStatusEvent.PlayerStatus.PLAYING :
                MediaPlayerStatusEvent.PlayerStatus.STOPPED;
        mediaPlayerStatus.playedStream = selectedStream;

        EventBus.INSTANCE.fire(mediaPlayerStatus);
    }

    private void handlePhoneCallEvent(PhoneCallStatusEvent event) {
        if (playerState != PlayerState.PREPARING && playerState != PlayerState.PLAYING) {
            return;
        }

        switch(event.callState) {
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mediaPlayerHolder.setVolume(0);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                mediaPlayerHolder.setVolume(volume);
                break;
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

        notifyPlayerStatusChange(null);

        shutDownMediaPlayer(mediaPlayerHolder);

        changeState(PlayerState.STOPPED);
    }

    private void shutDownMediaPlayer(MediaPlayerHolder mediaPlayer) {
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

            notifyPlayerStatusChange(null);

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

        result.add(PhoneCallStatusEvent.class);

        result.add(RequestMediaPlayerStatusEvent.class);

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
        EventBus.INSTANCE.fire(new MediaPlayerComponentStatusEvent(playerState));
        this.playerState = playerState;
    }
}
