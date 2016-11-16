package com.samebug.clients.common.entities.search;

public class Requested implements SearchRequest {
    private final String trace;

    public Requested(String trace) {
        this.trace = trace;
    }

    @Override
    public String getTrace() {
        return trace;
    }
}
