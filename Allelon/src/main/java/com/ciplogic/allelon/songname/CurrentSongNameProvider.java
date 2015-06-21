package com.ciplogic.allelon.songname;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.MediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.MediaPlayerTitleEvent;

import java.util.Collections;
import java.util.List;

/**
 * Listens for media player status events and runs on a parallel thread the fetching of the
 * current song title every 15 seconds.
 */
public class CurrentSongNameProvider implements EventListener {
    private volatile String currentTitle;
    private CurrentSongFetcherThread currentSongFetcherThread;

    public CurrentSongNameProvider() {
        EventBus.INSTANCE.registerListener(this);
    }

    @Override
    public void onEvent(Event event) {
        MediaPlayerStatusEvent mediaPlayerStatus = (MediaPlayerStatusEvent) event;

        if (!mediaPlayerStatus.playing) {
            setUrl(null);
        } else {
            setUrl(mediaPlayerStatus.playedStream.getTitleUrl());
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        return (List) Collections.singletonList(MediaPlayerStatusEvent.class);
    }

    public void setUrl(String titleUrl) {
        currentTitle = titleUrl == null? "" : "..."; // when the URL changes, we need to reload the title.

        if (currentSongFetcherThread != null) {
            currentSongFetcherThread.stop();
            currentSongFetcherThread = null;
        }

        if (titleUrl != null) {
            currentSongFetcherThread = new CurrentSongFetcherThread(this, titleUrl);
        }
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setFetchedTitle(String title) {
        String oldTitle = currentTitle;
        currentTitle = title;

        if (!areStringsEquals(oldTitle, currentTitle)) {
            EventBus.INSTANCE.fire(new MediaPlayerTitleEvent(currentTitle));
        }
    }

    private boolean areStringsEquals(String oldTitle, String currentTitle1) {
        if (oldTitle == null) {
            return currentTitle1 == null;
        }

        return oldTitle.equals(currentTitle1);
    }
}
