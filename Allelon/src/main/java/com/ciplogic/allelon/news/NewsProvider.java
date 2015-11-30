package com.ciplogic.allelon.news;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.news.ReceivedNewsEvent;
import com.ciplogic.allelon.eventbus.events.news.RequestNewsEvent;
import com.ciplogic.allelon.remote.HttpAsyncRequest;
import com.ciplogic.allelon.remote.HttpResponseCallback;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetch the news from the remote Allelon server. Allows reading them as messages.
 * Caches the messages for some time.
 */
public class NewsProvider implements EventListener {
    private long lastDataFetched = -1;
    public static final long DELAY = 10L * 60 * 1000; // 10 minutes for news refetch

    public static final String URL= "http://allelon.at/feed/";
    public static final String URL_DATA_FAILED= "http://allelon.at/feed/#failure";

    private static Pattern HTML_ENTITY_PATTERN = Pattern.compile("&#(\\d+);");

    private List<NewsVo> data;

    public NewsProvider() {
        data = new ArrayList<>();
        data.add(new NewsVo("Loading...", "Fetching data.", "Allelon", URL));

        EventBus.INSTANCE.registerListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (System.currentTimeMillis() - DELAY > lastDataFetched) {
            requestRemoteData();
        }

        // respond imediatelly, even with the data from the feed.
        EventBus.INSTANCE.fire(new ReceivedNewsEvent(data));
    }

    private void requestRemoteData() {
        new HttpAsyncRequest(URL, new HttpResponseCallback() {
            @Override
            public void onResult(String content) {
                populateDataFromNewsFeedXml(content);
                EventBus.INSTANCE.fire(new ReceivedNewsEvent(data));
            }

            @Override
            public void onReject(Throwable e) {
                populateFailureReadingNewsData(e);
                EventBus.INSTANCE.fire(new ReceivedNewsEvent(data));
            }
        });
    }

    private void populateDataFromNewsFeedXml(String content) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(content));

            List<NewsVo> data = new ArrayList<>();

            int eventType = xpp.getEventType();
            NewsVo currentItem = new NewsVo();

            XmlNode currentNode = new XmlNode(xpp.getName(), xpp.getNamespace());

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentNode = new XmlNode(xpp.getName(), xpp.getNamespace());
                }
                if (eventType == XmlPullParser.END_TAG) {
                    currentNode = new XmlNode("", "");
                }

                if (eventType == XmlPullParser.START_TAG && "item".equals(xpp.getName()) && "".equals(xpp.getNamespace())) {
                    currentItem = new NewsVo();
                }

                if (eventType == XmlPullParser.TEXT && "title".equals(currentNode.name) && "".equals(currentNode.namespace)) {
                    currentItem.title = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.TEXT && "description".equals(currentNode.name) && "".equals(currentNode.namespace)) {
                    currentItem.description = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.TEXT && "link".equals(currentNode.name) && "".equals(currentNode.namespace)) {
                    currentItem.url = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.TEXT && "pubDate".equals(currentNode.name) && "".equals(currentNode.namespace)) {
                    currentItem.date = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.TEXT && "creator".equals(currentNode.name) && "http://purl.org/dc/elements/1.1/".equals(currentNode.namespace)) {
                    currentItem.author = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.TEXT && "comments".equals(currentNode.name) && "http://purl.org/rss/1.0/modules/slash/".equals(currentNode.namespace)) {
                    currentItem.commentCount = resolveXmlEntities(xpp.getText());
                }

                if (eventType == XmlPullParser.END_TAG && "item".equals(xpp.getName()) && "".equals(xpp.getNamespace())) {
                    data.add(currentItem);
                }

                eventType = xpp.next();
            }

            this.lastDataFetched = System.currentTimeMillis();
            this.data = data;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse news feed: " + e.getMessage(), e);
        }
    }

    /**
     * Since the XML parser from Android doesn't resolves the entities, we need to do that
     * manually. We will just iterate over all the entities &#\d+; and replace them with
     * the Unicode character the entity is pointing at.
     *
     * @param content
     * @return
     */
    private String resolveXmlEntities(String content) {
        StringBuilder result = new StringBuilder("");

        int currentIndex = 0;

        Matcher matcher = HTML_ENTITY_PATTERN.matcher(content);
        while (matcher.find()) {
            result.append(content.substring(currentIndex, matcher.start()))
                .append((char) Integer.parseInt(matcher.group(1)));
            currentIndex = matcher.end();
        }

        result.append(content.substring(currentIndex, content.length()));

        return result.toString();
    }

    private void populateFailureReadingNewsData(Throwable e) {
        lastDataFetched = System.currentTimeMillis() + DELAY;
        data = new ArrayList<>();
        data.add(new NewsVo("Loading Failed",
                "Fetching remote data failed: " + e.getMessage(),
                "Allelon",
                URL_DATA_FAILED));
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        return (List) Collections.singletonList(RequestNewsEvent.class);
    }
}
