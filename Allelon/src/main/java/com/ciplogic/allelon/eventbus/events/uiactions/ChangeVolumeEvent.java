package com.ciplogic.allelon.eventbus.events.uiactions;

import com.ciplogic.allelon.eventbus.Event;

public class ChangeVolumeEvent extends Event {
    public final int value;

    public ChangeVolumeEvent(int value) {
        this.value = value;
    }
}
