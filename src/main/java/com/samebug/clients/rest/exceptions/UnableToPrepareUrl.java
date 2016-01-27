package com.samebug.clients.rest.exceptions;

import java.io.IOException;

public class UnableToPrepareUrl extends SamebugClientException {
    public UnableToPrepareUrl(String url, IOException e) {
        super("Unable to prepare url "+ url, e);
    }
}
