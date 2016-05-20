package com.samebug.clients.search.api.exceptions;

public class JsonParseException extends SamebugClientException {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }
}
