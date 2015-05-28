package com.ciplogic.allelon.eventbus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * <p>The event bus, like any normal event bus routes events, but dispatches them on a new worker
 * thread, in order to ensure that the one who raises the event is not blocked.</p>
 */
public class EventBus {
    public static EventBus INSTANCE = new EventBus();

    private Map<Class, Set<EventListener>> eventTypeToListeners = new HashMap<Class, Set<EventListener>>();

    private Queue<Event> eventsToFire = new LinkedList<Event>();
    private volatile boolean processingEvent;

    public EventBus() {
    }

    /**
     * Registers the new event in the queue of the events to be processed.
     * @param event
     * @param <T>
     */
    public <T extends Event> void fire(T event) {
        postEventToQueue(event);

        if (!processingEvent) {
            while (!eventsToFire.isEmpty()) {
                processNextEvent();
            }
        }
    }

    private void processNextEvent() {
        try {
            processingEvent = true;

            Event event = eventsToFire.remove();

            Set<EventListener> listeners = eventTypeToListeners.get(event.getClass());

            if (listeners != null && !listeners.isEmpty()) {
                for (EventListener listener : listeners) {
                    listener.onEvent(event);
                }
            } else {
                // no listeners are listening for the event
            }
        } finally {
            processingEvent = false;
        }
    }

    private <T extends Event> void postEventToQueue(T event) {
        synchronized (this) {
            if (event.removeDuplicates) { // cleanup previous events if needed.
                Iterator<Event> iterator = eventsToFire.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getClass() == event.getClass()) {
                        iterator.remove();
                    }
                }
            }

            eventsToFire.add(event);
        }
    }

    public void registerListener( EventListener listener ) {
        List<Class<? extends Event>> listenedEvents = listener.getListenedEvents();

        for (Class<? extends Event> listenedEvent : listenedEvents) {
            Set<EventListener> eventListeners = eventTypeToListeners.get(listenedEvent);

            if (eventListeners == null) {
                eventListeners = new LinkedHashSet<EventListener>();
                eventTypeToListeners.put(listenedEvent, eventListeners);
            }

            if (eventListeners.contains(listener)) {
                throw new IllegalStateException("Listener already registered: " + listener);
            }

            eventListeners.add(listener);
        }
    }

    public void unregisterListener( EventListener listener ) {
        for (Class<? extends Event> listenedEventType : listener.getListenedEvents()) {
            eventTypeToListeners.get(listenedEventType).remove(listener);
        }
    }
}
