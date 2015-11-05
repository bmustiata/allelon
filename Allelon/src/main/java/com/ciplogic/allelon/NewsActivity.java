package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.ciplogic.allelon.lastplay.LastPlayChangeListener;
import com.ciplogic.allelon.lastplay.LastPlayProvider;
import com.ciplogic.allelon.lastplay.LastPlayedDataAdapter;
import com.ciplogic.allelon.player.AvailableStream;

public class NewsActivity extends Activity implements LastPlayChangeListener, StreamChangedListener {
    private ListView newsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_activity);

        LastPlayProvider.INSTANCE.getSongList();

        findUiComponents();
        addEventListeners();
    }

    @Override
    protected void onDestroy() {
        LastPlayProvider.INSTANCE.removePlayListener();
        SelectedStream.removeStreamChangeListener(this);

        super.onDestroy();
    }

    private void findUiComponents() {
        newsListView = (ListView) findViewById(R.id.newsListView);
    }

    private void addEventListeners() {
        LastPlayProvider.INSTANCE.addPlayListListener(NewsActivity.this);
        SelectedStream.addStreamChangeListener(this);

        // FIXME: apparently there is a bug in the listview, that doesn't allow the adapter to already have data
        // and then be assigned to the ListView.
        LastPlayedDataAdapter lastPlayedDataAdapter = new LastPlayedDataAdapter(this, newsListView);
        newsListView.setAdapter(lastPlayedDataAdapter);
    }

    @Override
    public void onChange() {
        ((LastPlayedDataAdapter) newsListView.getAdapter()).reload();
    }

    @Override
    public void onStreamChange(AvailableStream stream) {
        LastPlayProvider.INSTANCE.removePlayListener();
        LastPlayProvider.INSTANCE.addPlayListListener(NewsActivity.this);
    }
}
