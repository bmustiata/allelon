package com.ciplogic.allelon.remote;

public class HttpPoll implements Runnable {
    private final String url;
    private final HttpResponseCallback callback;
    private final Integer delay;

    private volatile boolean fetching = true;

    public HttpPoll(String url, Integer delay, HttpResponseCallback callback) {
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
            String content = new HttpRequest(url).readContent();
            if (fetching) {
                callback.onResult(content);
            }

            synchronized (this) {
                try {
                    wait(this.delay);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
