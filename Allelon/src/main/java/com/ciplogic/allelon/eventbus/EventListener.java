package com.ciplogic.allelon.eventbus;

import java.util.List;

public interface EventListener {
    public void onEvent(Event event);

    public List<Class<? extends Event>> getListenedEvents();
}
