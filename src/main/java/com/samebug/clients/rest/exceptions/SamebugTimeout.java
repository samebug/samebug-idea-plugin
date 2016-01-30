package com.samebug.clients.rest.exceptions;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

public class SamebugTimeout extends SamebugClientException {
    public SamebugTimeout(InterruptedIOException e) {
        super(e);
    }
}
