package com.ciplogic.allelon.news;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ciplogic.allelon.R;
import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.news.ReceivedNewsEvent;

import java.util.Collections;
import java.util.List;

/**
 * Data adapter for the news.
 */
public class NewsDataAdapter extends BaseAdapter implements EventListener {
    private Activity activity;

    private List<NewsVo> data = Collections.emptyList();

    public NewsDataAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsVo newsItem = data.get(position);
        View result = activity.getLayoutInflater().inflate(R.layout.news_entry, null);

        ((TextView) result.findViewById(R.id.info_title)).setText(newsItem.title);
        ((TextView) result.findViewById(R.id.info_text)).setText( newsItem.description);
        ((TextView) result.findViewById(R.id.info_author)).setText( newsItem.author);
        ((TextView) result.findViewById(R.id.info_url)).setText( newsItem.url);
        ((Button) result.findViewById(R.id.info_read_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("yay");
            }
        });

        return result;
    }

    @Override
    public void onEvent(Event event) {
        final ReceivedNewsEvent receivedNewsEvent = (ReceivedNewsEvent) event;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data = receivedNewsEvent.news;
                NewsDataAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        return (List) Collections.singletonList(ReceivedNewsEvent.class);
    }
}
