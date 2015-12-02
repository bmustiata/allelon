package com.ciplogic.allelon;

import com.ciplogic.allelon.player.AvailableStream;

import java.util.HashSet;
import java.util.Set;

public class SelectedStream {
    private static AvailableStream selectedStream = AvailableStream.ALLELON_RO;
    private static Set<StreamChangedListener> streamChangedListeners = new HashSet<StreamChangedListener>();

    public static AvailableStream getSelectedStream() {
        return selectedStream;
    }

    public static void setSelectedStream(AvailableStream selectedStream) {
        SelectedStream.selectedStream = selectedStream;

        for (StreamChangedListener listener : streamChangedListeners) {
            listener.onStreamChange(selectedStream);
        }
    }

    public static void addStreamChangeListener(StreamChangedListener listener) {
        streamChangedListeners.add(listener);
    }

    public static void removeStreamChangeListener(StreamChangedListener listener) {
        streamChangedListeners.remove(listener);
    }
}
