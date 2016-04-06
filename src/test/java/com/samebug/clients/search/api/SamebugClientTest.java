package com.samebug.clients.search.api;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * Created by poroszd on 3/11/16.
 */
public class SamebugClientTest {
    @Test
    public void checkEndpoint() {
        assertEquals(URI.create("https://samebug.io/"), SamebugClient.root);
    }

    @Test
    public void checkTrackingEndpoint() {
        assertEquals(URI.create("https://samebug.io/").resolve("track/trace"), SamebugClient.trackingGateway);
    }
}
