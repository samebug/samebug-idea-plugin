package com.samebug.clients.rest.exceptions;

public class SamebugClientError extends Error {
    public SamebugClientError() {
        super();
    }

    public SamebugClientError(String message) {
        super(message);
    }

    public SamebugClientError(String message, Throwable cause) {
        super(message, cause);
    }

    public SamebugClientError(Throwable cause) {
        super(cause);
    }
}
