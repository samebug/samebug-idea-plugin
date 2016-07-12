package com.samebug.clients.search.api.client;

public class Success<T> implements ClientResponse<T> {

    private ConnectionStatus connectionStatus;
    private T response;

    public Success(ConnectionStatus connectionStatus, T response) {
        this.connectionStatus = connectionStatus;
        this.response = response;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public T getResponse() {
        return response;
    }
}
