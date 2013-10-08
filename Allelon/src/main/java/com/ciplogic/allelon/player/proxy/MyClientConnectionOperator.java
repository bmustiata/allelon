package com.ciplogic.allelon.player.proxy;

import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;

public class MyClientConnectionOperator extends DefaultClientConnectionOperator {
    public MyClientConnectionOperator(final SchemeRegistry sr) {
        super(sr);
    }

    @Override
    public OperatedClientConnection createConnection() {
        return new MyClientConnection();
    }
}
