package com.samebug.clients.http.client;

public abstract class TestWithSamebugClient {
    protected final SamebugClient unauthenticatedClient = new SamebugClient(
            new Config(
                    null, null,
                    "http://localhost:9000", null, false,
                    5000, 10000, false, true,
                    null
            ), null);

    protected final SamebugClient authenticatedClient = new SamebugClient(
            new Config(
                    "cbac9adc-9466-4dbc-8033-d94135cac855", 1,
                    "http://localhost:9000", null, false,
                    5000, 10000, false, true,
                    null
            ), null);

}
