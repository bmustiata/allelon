package com.ciplogic.allelon.player;

public enum AvailableStream {
    CONTEMPORAN("http://87.118.82.77:8000/stream/1/", "Contemporan"),
    CLASSIC("http://87.118.82.77:8000/stream/2/", "Classic"),
    CONTEMPORAN_AAC("http://87.118.82.77:8000/stream/3/", "Contemporan AAC");


    private final String url;
    private final String label;

    AvailableStream(String url, String label) {
        this.url = url;
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public static AvailableStream fromLabel(String label) {
        for (AvailableStream availableStream : AvailableStream.values()) {
            if (availableStream.label.equals(label)) {
                return availableStream;
            }
        }

        return null;
    }
}
