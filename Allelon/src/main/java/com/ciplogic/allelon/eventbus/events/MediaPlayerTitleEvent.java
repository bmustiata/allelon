package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.Event;

public class MediaPlayerTitleEvent extends Event {
    public String title;

    public MediaPlayerTitleEvent(String title) {
        this.title = title;
    }
}
