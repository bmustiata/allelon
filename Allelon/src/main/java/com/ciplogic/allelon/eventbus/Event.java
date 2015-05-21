package com.ciplogic.allelon.eventbus;

public class Event {
    /**
     * When set to true, the event bus will remove all the previous events
     * of the same class from the queue.
     */
    public boolean removeDuplicates;
}
