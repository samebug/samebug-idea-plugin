package com.samebug.clients.http.client;

public abstract class TestWithSamebugClient {
    protected final SamebugClient client = new SamebugClient(
            new Config(
                    "apikey", 1,
                    "https://nightly.samebug.com", null, false,
                    5000, 10000, false,
                    null
            ), null);
}
