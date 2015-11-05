package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.events.news.RequestNewsEvent;
import com.ciplogic.allelon.news.NewsDataAdapter;

public class NewsActivity extends Activity {
    private ListView newsListView;
    private NewsDataAdapter newsDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_activity);

        findUiComponents();
        addEventListeners();

        EventBus.INSTANCE.registerListener(newsDataAdapter);
        EventBus.INSTANCE.fire(new RequestNewsEvent());
    }

    @Override
    protected void onDestroy() {
        EventBus.INSTANCE.unregisterListener(newsDataAdapter);

        super.onDestroy();
    }

    private void findUiComponents() {
        newsListView = (ListView) findViewById(R.id.newsListView);
    }

    private void addEventListeners() {
        newsDataAdapter = new NewsDataAdapter(this);
        newsListView.setAdapter(newsDataAdapter);
    }
}
