package org.bloggers.ts_users.config.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream inputStream;

    /**
     * Constructor to initialize the cached body.
     *
     * @param cachedBody input stream.
     */
    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.inputStream = new ByteArrayInputStream(cachedBody);
    }

    /**
     * Check if the input stream is finished.
     *
     * @return boolean value.
     */
    @Override
    public boolean isFinished() {
        try {
            return inputStream.available() == 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Check if the input stream is ready.
     */
    @Override
    public boolean isReady() {
        return true;
    }

    /**
     * Set the read org.bloggers.ts_users.listener.
     *
     * @param readListener read org.bloggers.ts_users.listener
     * @throws UnsupportedOperationException unsupported operation exception.
     */

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Read the input stream.
     * @return int value.
     * @throws UnsupportedOperationException unsupported operation exception.
     */
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
