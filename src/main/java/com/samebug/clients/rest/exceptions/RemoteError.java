package com.samebug.clients.rest.exceptions;

public class RemoteError extends SamebugClientException {

    public RemoteError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return "Unable to requestJson request, errors in header: " + errorMessage;
    }

    private final String errorMessage;

}
