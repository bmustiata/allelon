package com.ciplogic.allelon.remote;

public interface HttpResponseCallback {
    void onResult(String content);
    void onReject(Throwable e);
}
