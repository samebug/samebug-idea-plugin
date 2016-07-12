package com.samebug.clients.search.api.client;

import com.samebug.clients.search.api.exceptions.SamebugClientException;

public class Failure<T> implements ClientResponse<T> {
    private ConnectionStatus connectionStatus;
    private SamebugClientException exception;

    public Failure(ConnectionStatus connectionStatus, SamebugClientException exception) {
        this.connectionStatus = connectionStatus;
        this.exception = exception;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public SamebugClientException getException() {
        return exception;
    }
}
