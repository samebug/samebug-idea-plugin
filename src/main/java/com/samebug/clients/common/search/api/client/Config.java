/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.search.api.client;

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
