package com.ciplogic.allelon.proxy;

import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public class IcyLineParser extends BasicLineParser {
    private static final String ICY_PROTOCOL_NAME = "ICY";
    public IcyLineParser() {
        super();
    }

    @Override
    public boolean hasProtocolVersion(CharArrayBuffer buffer,
                                      ParserCursor cursor) {
        boolean superFound = super.hasProtocolVersion(buffer, cursor);
        if (superFound) {
            return true;
        }
        int index = cursor.getPos();

        final int protolength = ICY_PROTOCOL_NAME.length();

        if (buffer.length() < protolength)
            return false; // not long enough for "HTTP/1.1"

        if (index < 0) {
            // end of line, no tolerance for trailing whitespace
            // this works only for single-digit major and minor version
            index = buffer.length() - protolength;
        } else if (index == 0) {
            // beginning of line, tolerate leading whitespace
            while ((index < buffer.length()) &&
                    HTTP.isWhitespace(buffer.charAt(index))) {
                index++;
            }
        } // else within line, don't tolerate whitespace

        return index + protolength <= buffer.length() &&
                buffer.substring(index, index + protolength).equals(ICY_PROTOCOL_NAME);

    }


    @Override
    public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer,
                                                ParserCursor cursor) throws ParseException {

        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }

        final int protolength = ICY_PROTOCOL_NAME.length();

        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();

        skipWhitespace(buffer, cursor);

        int i = cursor.getPos();

        // long enough for "HTTP/1.1"?
        if (i + protolength + 4 > indexTo) {
            throw new ParseException
                    ("Not a valid protocol version: " +
                            buffer.substring(indexFrom, indexTo));
        }

        // check the protocol name and slash
        if (!buffer.substring(i, i + protolength).equals(ICY_PROTOCOL_NAME)) {
            return super.parseProtocolVersion(buffer, cursor);
        }

        cursor.updatePos(i + protolength);

        return createProtocolVersion(1, 0);
    }

    @Override
    public StatusLine parseStatusLine(CharArrayBuffer buffer,
                                      ParserCursor cursor) throws ParseException {
        return super.parseStatusLine(buffer, cursor);
    }
}
