package com.ciplogic.allelon.remote;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpPoll implements Runnable {
    private final String url;
    private final HttpPollCallback callback;
    private final Integer delay;

    private volatile boolean fetching = true;

    public HttpPoll(String url, Integer delay, HttpPollCallback callback) {
        this.url = url;
        this.callback = callback;
        this.delay = delay;

        new Thread(this).start();
    }

    public synchronized void stop() {
        fetching = false;
        notify();
    }

    @Override
    public void run() {
        while (fetching) {
            String content = readContent();
            if (fetching) {
                callback.run(content);
            }

            synchronized (this) {
                try {
                    wait(this.delay);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private String readContent() {
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
