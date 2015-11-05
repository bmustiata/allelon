package com.ciplogic.allelon.remote;

/**
 * Does a single HTTP request, that gets notified asynchronously.
 */
public class HttpAsyncRequest implements Runnable {
    private final String url;
    private final HttpResponseCallback responseCallback;

    public HttpAsyncRequest(String url, HttpResponseCallback responseCallback) {
        this.url = url;
        this.responseCallback = responseCallback;

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            String content = new HttpRequest(url).readContent();
            responseCallback.onResult(content);
        } catch (Throwable t) {
            responseCallback.onReject(t);
        }
    }
}
