package com.samebug.clients.rest.exceptions;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.InterruptedIOException;

public class SamebugConnectTimeout extends SamebugTimeout {
    public SamebugConnectTimeout(InterruptedIOException e) {
        super(e);
    }
}
