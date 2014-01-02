package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ciplogic.allelon.lastplay.LastPlayChangeListener;
import com.ciplogic.allelon.lastplay.LastPlayProvider;
import com.ciplogic.allelon.lastplay.Song;

public class LastPlayedActivity extends Activity implements LastPlayChangeListener {
    private ListView lastPlayedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.last_played_activity);

        LastPlayProvider.INSTANCE.getSongList();

        findUiComponents();
        addEventListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LastPlayProvider.INSTANCE.removePlayListener();
    }

    private void findUiComponents() {
        lastPlayedListView = (ListView) findViewById(R.id.lastPlayedListView);
    }

    private void addEventListeners() {
        lastPlayedListView.setAdapter(createDataAdaptor());
        LastPlayProvider.INSTANCE.addPlayListListener(this);
    }

    @Override
    public void onChange() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastPlayedListView.setAdapter(createDataAdaptor());
            }
        });
    }

    private BaseAdapter createDataAdaptor() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return LastPlayProvider.INSTANCE.getSongList().size();
            }

            @Override
            public Song getItem(int position) {
                return LastPlayProvider.INSTANCE.getSongList().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View result = LastPlayedActivity.this.getLayoutInflater().inflate(R.layout.song_entry, null);

                ((TextView) result.findViewById(R.id.titleTextView)).setText( this.getItem(position).getTitle() );
                ((TextView) result.findViewById(R.id.authorTextView)).setText( this.getItem(position).getAuthor() );
                ((TextView) result.findViewById(R.id.timeTextView)).setText( this.getItem(position).getTime() );

                return result;
            }
        };
    }
}
