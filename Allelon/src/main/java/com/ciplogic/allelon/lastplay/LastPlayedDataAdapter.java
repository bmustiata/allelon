package com.ciplogic.allelon.lastplay;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ciplogic.allelon.R;

import java.util.ArrayList;
import java.util.List;

public class LastPlayedDataAdapter extends BaseAdapter {
    private final Activity activity;
    private List<Song> songList = new ArrayList<Song>();

    private boolean canLoadData = false;

    public LastPlayedDataAdapter(Activity activity, ListView listView) {
        this.activity = activity;

        checkForDataReloadAvailability(listView);
    }

    /*
     This is a terrible hack, that makes sure the scroll event was fired from the list view,
     before attempting to actually display data. The scroll event is fired after layouting.
     The problem is that if the data is changed after the construction of the list, but before
     layouting of the children, it will fail with a funny error.
     */
    private void checkForDataReloadAvailability(ListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                canLoadData = true;
                reload();
            }
        });
    }

    public void reload() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canLoadData) {
                    songList = LastPlayProvider.INSTANCE.getSongList();
                    LastPlayedDataAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Song getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = activity.getLayoutInflater().inflate(R.layout.song_entry, null);

        ((TextView) result.findViewById(R.id.titleTextView)).setText( this.getItem(position).getTitle() );
        ((TextView) result.findViewById(R.id.authorTextView)).setText( this.getItem(position).getAuthor() );
        ((TextView) result.findViewById(R.id.timeTextView)).setText( this.getItem(position).getTime() );

        return result;
    }
}
