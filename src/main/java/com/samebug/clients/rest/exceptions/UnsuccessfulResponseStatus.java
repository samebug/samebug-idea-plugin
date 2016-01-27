package com.samebug.clients.rest.exceptions;

public class UnsuccessfulResponseStatus extends SamebugClientException {
    private int statusCode;

    public UnsuccessfulResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return "Unable to requestJson request, status: " + statusCode;
    }
}
