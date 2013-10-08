package com.ciplogic.allelon.player;

public enum AvailableStream {
    CONTEMPORAN("http://87.118.82.77:8000/stream/1/"),
    CLASSIC("http://87.118.82.77:8000/stream/2/");

    private final String url;

    AvailableStream(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
