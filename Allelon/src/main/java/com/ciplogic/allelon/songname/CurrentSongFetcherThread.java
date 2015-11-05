package com.ciplogic.allelon.songname;

import com.ciplogic.allelon.remote.HttpPoll;
import com.ciplogic.allelon.remote.HttpResponseCallback;

public class CurrentSongFetcherThread extends HttpPoll {
    public CurrentSongFetcherThread(final CurrentSongNameProvider currentSongNameProvider, String url) {
        super(url, 15000, new HttpResponseCallback() {
            @Override
            public void onResult(String content) {
                currentSongNameProvider.setFetchedTitle(content);
            }

            @Override
            public void onReject(Throwable e) {
                // ignore in case of failure, just keep the previous one.
            }
        });
    }
}
