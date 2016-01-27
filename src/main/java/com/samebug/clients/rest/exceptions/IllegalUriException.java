package com.samebug.clients.rest.exceptions;

import java.net.MalformedURLException;

public class IllegalUriException extends SamebugClientError {
    public IllegalUriException(String uri, MalformedURLException e) {
        super("Illegal uri: "+ uri, e);
    }
}
