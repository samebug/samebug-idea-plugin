package com.samebug.clients.rest.exceptions;

public class UserUnauthorized extends SamebugClientException {

    @Override
    public String getMessage() {
        return "User is unauthorized";
    }
}
