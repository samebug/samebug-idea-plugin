package com.samebug.clients.http.exceptions;

public class SamebugException extends java.lang.Exception {
    public SamebugException() {
        super();
    }

    public SamebugException(String message) {
        super(message);
    }

    public SamebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamebugException(Throwable cause) {
        super(cause);
    }
}
