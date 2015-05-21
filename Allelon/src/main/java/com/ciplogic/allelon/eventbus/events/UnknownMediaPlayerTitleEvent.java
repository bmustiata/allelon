package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.events.MediaPlayerTitleEvent;

public class UnknownMediaPlayerTitleEvent extends MediaPlayerTitleEvent {
    public UnknownMediaPlayerTitleEvent() {
        title = "?";
    }
}
