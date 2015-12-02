package com.ciplogic.allelon.player;

/**
 * The available streams for the application.
 *
 * In case the streams change make sure to update:
 *
 * 0. This file, of course, lol :)
 * 1. PlayActivity.java, including the (mod 4) or whatever number is in there.
 * 2. land/play_activity.xml, layout/play_activity.xml
 *
 */
public enum AvailableStream {
    ALLELON_RO("http://allelon.at:8000/ro.mp3",
            "Allelon Ro",
            "http://allelon.at/icecast/now-playing.php?mount=/ro.mp3",
            "http://allelon.at:8000/played.html?sid=1"),

    ALLELON_LIFE("http://allelon.at:8000/life.mp3",
            "Allelon LIFE",
            "http://allelon.at/icecast/now-playing.php?mount=/life.mp3",
            "http://allelon.at:8000/played.html?sid=2"),

    ALLELON_CLASSIC("http://allelon.at:8000/classic.mp3",
            "Allelon Classic",
            "http://allelon.at/icecast/now-playing.php?mount=/classic.mp3",
            "http://allelon.at:8000/played.html?sid=3"),

    ALLELON_ARABIC("http://allelon.at:8000/arab.mp3",
            "Allelon Arab",
            "http://allelon.at/icecast/now-playing.php?mount=/arab.mp3",
            "http://allelon.at:8000/played.html?sid=8"),

    ALLELON_RO_AAC("http://allelon.at:8000/ro.mp3",
            "Allelon Ro",
            "http://allelon.at/icecast/now-playing.php?mount=/ro.mp3",
            "http://allelon.at:8000/played.html?sid=1"),


    ALLELON_LIFE_AAC("http://allelon.at:8000/life_32k.aac",
                     "Allelon LIFE",
                     "http://allelon.at/icecast/now-playing.php?mount=/life.mp3",
                     "http://allelon.at:8000/played.html?sid=2"),
    ;

    private final String url;
    private final String titleUrl;
    private final String historyUrl;

    /**
     * The available stream. While the label is not used, is kept so we know what goes where.
     * @param url
     * @param label
     * @param titleUrl
     * @param historyUrl
     */
    AvailableStream(String url, String label, String titleUrl, String historyUrl) {
        this.url = url;
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

    public static AvailableStream fromUrl(String url) {
        for (AvailableStream availableStream : AvailableStream.values()) {
            if (availableStream.url.equals(url)) {
                return availableStream;
            }
        }

        return null;
    }
}
