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
    ALLELON("http://radio.allelon.at/allelonat.mp3",
            "Allelon",
            "http://embed.bisericilive.com/audiocurrentstats?src=http%3A%2F%2Fradio.allelon.at%2Fallelonat.mp3",
            "http://allelon.at:8000/played.html?sid=1"),

    ALLELON_RO("http://ro.radio.allelon.at/roallelonat.mp3",
            "Allelon Ro",
            "http://embed.bisericilive.com/audiocurrentstats?src=http%3A%2F%2Fro.radio.allelon.at%2Froallelonat.mp3",
            "http://allelon.at:8000/played.html?sid=2"),

    ALLELON_TURK("http://shema.radio.allelon.at/shemaallelonat.mp3",
            "Allelon Turk (Shema)",
            "http://embed.bisericilive.com/audiocurrentstats?src=http%3A%2F%2Fshema.radio.allelon.at%2Fshemaallelonat.mp3",
            "http://allelon.at:8000/played.html?sid=8")
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
