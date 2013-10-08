package com.ciplogic.allelon.proxy;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

public class MyClientConnManager extends SingleClientConnManager {
    public MyClientConnManager(HttpParams params, SchemeRegistry schreg) {
        super(params, schreg);
    }

    @Override
    protected ClientConnectionOperator createConnectionOperator(
            final SchemeRegistry sr) {
        return new MyClientConnectionOperator(sr);
    }
}
