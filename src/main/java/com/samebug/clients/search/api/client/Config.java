package com.samebug.clients.search.api.client;

import org.jetbrains.annotations.Nullable;

public class Config {
    public String apiKey;
    @Nullable
    public Long workspaceId;
    public String serverRoot;
    public String trackingRoot;
    public boolean isTrackingEnabled;
    public int connectTimeout;
    public int requestTimeout;
    public boolean isApacheLoggingEnabled;

    public Config() {
    }

    public Config(final Config rhs) {
        this.apiKey = rhs.apiKey;
        this.workspaceId = rhs.workspaceId;
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
    }
}
