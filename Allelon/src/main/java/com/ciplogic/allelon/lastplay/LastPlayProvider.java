package com.ciplogic.allelon.lastplay;

import android.util.Log;

import com.ciplogic.allelon.SelectedStream;
import com.ciplogic.allelon.remote.HttpPoll;
import com.ciplogic.allelon.remote.HttpPollCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastPlayProvider {
    public static final LastPlayProvider INSTANCE = new LastPlayProvider();
    private LastPlayChangeListener lastPlayChangeListener;
    private HttpPoll httpPoll;

    private List<Song> songList = new ArrayList<Song>();

    // <td>05:22:29</td><td>agape - FECIOARA<t
    private Pattern LAST_PLAYED_PATTERN = Pattern.compile("<td>(\\d\\d:\\d\\d:\\d\\d)</td><td>(.*?)<");
    private Pattern AUTHOR_TITLE_PATTERN = Pattern.compile("^\\s*(.*?)\\s*-\\s*(.*)\\s*$");

    public LastPlayProvider() {
        markSongListLoading();
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void addPlayListListener(LastPlayChangeListener lastPlayChangeListener) {
        this.lastPlayChangeListener = lastPlayChangeListener;

        if (httpPoll != null) {
            markSongListLoading();
            httpPoll.stop();
        }

        httpPoll = new HttpPoll(SelectedStream.getSelectedStream().getHistoryUrl(), 60000, new HttpPollCallback() {
            @Override
            public void run(String content) {
                if (content == null) {
                    Log.w("Allelon", "Unable to read URL.");
                    return;
                }

                Log.d("Allelon", content);

                parsePlayedSongs(content);

                // the view is changed, and the polling works but there is no listener active.
                if (LastPlayProvider.this.lastPlayChangeListener != null) {
                    LastPlayProvider.this.lastPlayChangeListener.onChange();
                } else {
                    markSongListLoading();
                    httpPoll.stop();
                }
            }
        });
    }

    private void parsePlayedSongs(String content) {
        songList.clear();

        Matcher matcher = LAST_PLAYED_PATTERN.matcher(content);

        while (matcher.find()) {
            Song song = new Song();

            song.setTime(matcher.group(1));
            fillAuthorAndTitle(song, matcher.group(2));

            songList.add(song);
        }
    }

    private void fillAuthorAndTitle(Song song, String authorAndTitle) {
        Matcher matcher = AUTHOR_TITLE_PATTERN.matcher(authorAndTitle);

        if (matcher.find()) {
            song.setAuthor(matcher.group(1));
            song.setTitle(matcher.group(2));
        } else {
            song.setTitle(authorAndTitle);
            song.setAuthor("");
        }
    }

    public void removePlayListener() {
        this.lastPlayChangeListener = null;
        markSongListLoading();
        httpPoll.stop();
    }

    private void markSongListLoading() {
        songList.clear();

        Song song = new Song();
        song.setAuthor("Loading...");
        song.setTime("??:??:??");
        song.setTitle("");

        songList.add(song);
    }
}
