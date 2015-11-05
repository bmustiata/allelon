package com.ciplogic.allelon.eventbus.events.news;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.news.NewsVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data of the received news.
 */
public class ReceivedNewsEvent extends Event {
    public List<NewsVo> news;

    public ReceivedNewsEvent(List<NewsVo> news) {
        this.news = news;
    }
}
