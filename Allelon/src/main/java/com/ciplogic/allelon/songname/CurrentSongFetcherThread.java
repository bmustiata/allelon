package com.ciplogic.allelon.songname;

import com.ciplogic.allelon.remote.HttpPoll;
import com.ciplogic.allelon.remote.HttpPollCallback;

public class CurrentSongFetcherThread extends HttpPoll {
    public CurrentSongFetcherThread(final CurrentSongNameProvider currentSongNameProvider, String url) {
        super(url, 15000, new HttpPollCallback() {
            @Override
            public void run(String content) {
                currentSongNameProvider.setFetchedTitle(content);
            }
        });
    }
}
