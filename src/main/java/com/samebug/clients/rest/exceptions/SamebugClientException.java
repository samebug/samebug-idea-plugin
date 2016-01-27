package com.samebug.clients.rest.exceptions;

public class SamebugClientException extends Exception {
    public SamebugClientException() {
        super();
    }

    public SamebugClientException(String message) {
        super(message);
    }

    public SamebugClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamebugClientException(Throwable cause) {
        super(cause);
    }
}
