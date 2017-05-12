/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.http.client;

import org.jetbrains.annotations.Nullable;

public final class Config {
    @Nullable
    public final String apiKey;
    @Nullable
    public final Integer userId;
    @Nullable
    public final Integer workspaceId;
    public final String serverRoot;
    public final String trackingRoot;
    public final boolean isTrackingEnabled;
    public final int connectTimeout;
    public final int requestTimeout;
    public final boolean isApacheLoggingEnabled;
    public final boolean isJsonDebugEnabled;
    @Nullable
    public final ProxyConfig proxy;

    public Config(@Nullable String apiKey, @Nullable Integer userId, @Nullable Integer workspaceId,
                  String serverRoot, String trackingRoot, boolean isTrackingEnabled,
                  int connectTimeout, int requestTimeout, boolean isApacheLoggingEnabled,
                  boolean isJsonDebugEnabled, @Nullable ProxyConfig proxy) {
        this.apiKey = apiKey;
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.serverRoot = serverRoot;
        this.trackingRoot = trackingRoot;
        this.isTrackingEnabled = isTrackingEnabled;
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
        this.isApacheLoggingEnabled = isApacheLoggingEnabled;
        this.isJsonDebugEnabled = isJsonDebugEnabled;
        this.proxy = proxy;
    }

    public Config(final Config rhs) {
        this.apiKey = rhs.apiKey;
        this.userId = rhs.userId;
        this.workspaceId = rhs.workspaceId;
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        this.isJsonDebugEnabled = rhs.isJsonDebugEnabled;
        this.proxy = rhs.proxy == null ? null : new ProxyConfig(rhs.proxy);
    }
}
