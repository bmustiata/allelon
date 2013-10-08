package com.ciplogic.allelon.proxy;

import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.DefaultResponseParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;

public class MyClientConnection extends DefaultClientConnection {
    @Override
    protected HttpMessageParser createResponseParser(
            final SessionInputBuffer buffer,
            final HttpResponseFactory responseFactory, final HttpParams params) {
        return new DefaultResponseParser(buffer, new IcyLineParser(),
                responseFactory, params);
    }
}
