package com.samebug.clients.search.api.exceptions;

import com.samebug.clients.search.api.entities.RestError;

/**
 * Created by poroszd on 4/15/16.
 */
public class BadRequest extends SamebugClientException {
    private final RestError restError;

    public BadRequest(final RestError restError) {
        this.restError = restError;
    }

    public RestError getRestError() {
        return restError;
    }
}
