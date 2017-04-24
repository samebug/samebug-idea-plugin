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
                    "272fa735-866e-4b19-bafe-323c723c59d6", 8,
                    "http://localhost:9000", null, false,
                    5000, 10000, false, true,
                    null
            ), null);

}
