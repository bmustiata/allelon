package com.ciplogic.allelon.player;

public enum AvailableStream {
    ALLELON("http://allelon.at:8000/stream.mp3",
            "Allelon",
            "http://allelon.at/icecast/now-playing.php?mount=/stream.mp3",
            "http://allelon.at:8000/played.html?sid=1"),

    ALLELON_RO("http://allelon.at:8000/ro.mp3",
            "Allelon Ro",
            "http://allelon.at/icecast/now-playing.php?mount=/ro.mp3",
            "http://allelon.at:8000/played.html?sid=2"),

    ALLELON_CLASSIC("http://allelon.at:8000/classic.mp3",
            "Allelon Classic",
            "http://allelon.at/icecast/now-playing.php?mount=/classic.mp3",
            "http://allelon.at:8000/played.html?sid=3"),

    ALLELON_AAC("http://allelon.at:8000/stream/4/",
            "Allelon AAC",
            "http://allelon.at:8000/currentsong?sid=4",
            "http://allelon.at:8000/played.html?sid=4"),

    ALLELON_RO_AAC("http://allelon.at:8000/stream/5/",
            "Allelon Ro AAC",
            "http://allelon.at:8000/currentsong?sid=5",
            "http://allelon.at:8000/played.html?sid=5"),

    ALLELON_CLASSIC_AAC("http://allelon.at:8000/stream/6/",
            "Allelon Classic AAC",
            "http://allelon.at:8000/currentsong?sid=6",
            "http://allelon.at:8000/played.html?sid=6"),

    ALLELON_ARABIC("http://allelon.at:8000/arab.mp3",
            "Allelon Arabic",
            "http://allelon.at/icecast/now-playing.php?mount=/arab.mp3",
            "http://allelon.at:8000/played.html?sid=8");

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
