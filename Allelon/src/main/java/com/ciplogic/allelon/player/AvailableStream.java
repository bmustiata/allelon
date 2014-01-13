package com.ciplogic.allelon.player;

public enum AvailableStream {
    CONTEMPORAN("http://87.118.82.77:8000/stream/1/",
            "Contemporan",
            "http://allelon.at:8000/currentsong?sid=1",
            "http://allelon.at:8000/played.html?sid=1"),

    CLASSIC("http://87.118.82.77:8000/stream/2/",
            "Classic",
            "http://allelon.at:8000/currentsong?sid=2",
            "http://allelon.at:8000/played.html?sid=2"),

    CONTEMPORAN_AAC("http://87.118.82.77:8000/stream/3/",
            "Contemporan AAC",
            "http://allelon.at:8000/currentsong?sid=3",
            "http://allelon.at:8000/played.html?sid=3");


    private final String url;
    private final String label;
    private final String titleUrl;
    private final String historyUrl;

    AvailableStream(String url, String label, String titleUrl, String historyUrl) {
        this.url = url;
        this.label = label;
        this.titleUrl = titleUrl;
        this.historyUrl = historyUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public String getHistoryUrl() {
        return historyUrl;
    }

    public static AvailableStream fromLabel(String label) {
        for (AvailableStream availableStream : AvailableStream.values()) {
            if (availableStream.label.equals(label)) {
                return availableStream;
            }
        }

        return null;
    }

    public static AvailableStream fromUrl(String url) {
        for (AvailableStream availableStream : AvailableStream.values()) {
            if (availableStream.url.equals(url)) {
                return availableStream;
            }
        }

        return null;
    }
}
