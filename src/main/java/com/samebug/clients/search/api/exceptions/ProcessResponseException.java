package com.samebug.clients.search.api.exceptions;

public class ProcessResponseException extends SamebugClientException {
    public ProcessResponseException(String message) {
        super(message);
    }

    public ProcessResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessResponseException(Throwable cause) {
        super(cause);
    }
}
