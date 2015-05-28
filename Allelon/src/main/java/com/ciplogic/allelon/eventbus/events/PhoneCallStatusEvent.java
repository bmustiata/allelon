package com.ciplogic.allelon.eventbus.events;

import com.ciplogic.allelon.eventbus.Event;

/**
 * Event that gets raised when phone calls occur.
 */
public class PhoneCallStatusEvent extends Event {
    public int callState;

    public PhoneCallStatusEvent(int callState) {
        this.callState = callState;
    }
}
