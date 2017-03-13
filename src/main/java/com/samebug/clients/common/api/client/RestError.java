package com.samebug.clients.common.api.client;

public final class RestError extends BasicRestError {
    public RestError(String code, String message) {
        super(code, message);
    }
}
