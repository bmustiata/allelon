package com.ciplogic.allelon.eventbus.events.uiactions;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.player.AvailableStream;

public class SelectStreamEvent extends Event {
    public final AvailableStream availableStream;

    public SelectStreamEvent(AvailableStream selectedStream) {
        removeDuplicates = true;
        this.availableStream = selectedStream;
    }
}
