package com.ciplogic.allelon.songname;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CurrentSongFetcherThread implements Runnable {
    private final CurrentSongNameProvider currentSongNameProvider;
    private final String url;
    private volatile boolean fetching = true;

    public CurrentSongFetcherThread(CurrentSongNameProvider currentSongNameProvider, String url) {
        this.url = url;
        this.currentSongNameProvider = currentSongNameProvider;

        new Thread(this).start();
    }

    public synchronized void stop() {
        fetching = false;
        notify();
    }

    @Override
    public void run() {
        while (fetching) {
            String title = readTitle();
            if (fetching) {
                currentSongNameProvider.setFetchedTitle(title);
            }

            synchronized (this) {
                try {
                    wait(15000L);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private String readTitle() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();

                return responseString;
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            return null;
        }
    }
}
