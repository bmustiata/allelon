package com.ciplogic.allelon.songname;

import com.ciplogic.allelon.remote.HttpPoll;
import com.ciplogic.allelon.remote.HttpResponseCallback;

public class CurrentSongFetcherThread extends HttpPoll {
    public CurrentSongFetcherThread(final CurrentSongNameProvider currentSongNameProvider, String url) {
        super(url, 15000, new HttpResponseCallback() {
            @Override
            public void onResult(String content) {
                try {
                    content = new String(content.getBytes("windows-1252"), "utf-8");
                } catch (Exception e) {
                    // on purpose, because this code basically assumes that the remote title
                    // will be in a broken
                }

                // make sure we're not displaying all the metadata, but just the title.
                if (content != null) {
                    String[] tokens = content.split("[\\r\\n]+");
                    content = tokens[0];
                }

                // cleanup  for the content, since some streams send spaces in front.
                if (content != null) {
                    content = content.trim();
                }

                currentSongNameProvider.setFetchedTitle(content);
            }

            @Override
            public void onReject(Throwable e) {
                // ignore in case of failure, just keep the previous one.
            }
        });
    }
}
