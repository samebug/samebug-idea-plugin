package com.samebug.clients.rest.exceptions;

import java.net.SocketTimeoutException;

public class SamebugSocketTimeout extends SamebugTimeout {
    public SamebugSocketTimeout(SocketTimeoutException e) {
        super(e);
    }
}
