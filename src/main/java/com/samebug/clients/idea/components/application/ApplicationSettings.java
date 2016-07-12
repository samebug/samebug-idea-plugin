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
package com.samebug.clients.idea.components.application;

import com.samebug.clients.search.api.client.Config;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ApplicationSettings {
    //=========================================================================
    // NOTE: Make sure to extend equals and copy constructor when adding new fields!
    @Nullable
    public String apiKey;
    @Nullable
    public Long workspaceId = defaultWorkspaceId;
    public String instanceId = UUID.randomUUID().toString();
    public int userId;
    @Nullable
    public String avatarUrl;
    public String serverRoot = defaultServerRoot;
    public String trackingRoot = defaultTrackingRoot;
    public boolean isTrackingEnabled = defaultIsTrackingEnabled;
    public int connectTimeout = defaultConnectTimeout;
    public int requestTimeout = defaultRequestTimeout;
    public boolean isApacheLoggingEnabled = defaultIsApacheLoggingEnabled;
    public boolean showZeroSolutions = defaultShowZeroSolutions;
    public boolean showRecurring = defaultShowRecurring;

    //=========================================================================

    public static final Long defaultWorkspaceId = null;
    public static final String defaultServerRoot = "https://samebug.io";
    public static final String defaultTrackingRoot = defaultServerRoot + "/track/trace";
    public static final boolean defaultIsTrackingEnabled = true;
    public static final int defaultConnectTimeout = 5000;
    public static final int defaultRequestTimeout = 10000;
    public static final boolean defaultIsApacheLoggingEnabled = false;
    public static final boolean defaultShowZeroSolutions = true;
    public static final boolean defaultShowRecurring = true;

    public ApplicationSettings() {
    }

    public ApplicationSettings(final ApplicationSettings rhs) {
        this.apiKey = rhs.apiKey;
        this.workspaceId = rhs.workspaceId;
        this.instanceId = rhs.instanceId;
        this.userId = rhs.userId;
        this.avatarUrl = rhs.avatarUrl;
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        this.showZeroSolutions = rhs.showZeroSolutions;
        this.showRecurring = rhs.showRecurring;
    }

    @Override
    public int hashCode() {
        int apiKeyHash = apiKey == null ? 0 : apiKey.hashCode();
        return ((31 + apiKeyHash) * 31 + serverRoot.hashCode()) * 31 + trackingRoot.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof ApplicationSettings)) return false;
        else {
            final ApplicationSettings rhs = (ApplicationSettings) that;
            return ((rhs.apiKey == null && apiKey == null) || (rhs.apiKey != null && rhs.apiKey.equals(apiKey)))
                    && ((rhs.workspaceId == null && workspaceId == null) || (rhs.workspaceId != null && rhs.workspaceId.equals(workspaceId)))
                    && rhs.instanceId.equals(instanceId)
                    && rhs.userId == userId
                    && ((rhs.avatarUrl == null && avatarUrl == null) || (rhs.avatarUrl != null && rhs.avatarUrl.equals(avatarUrl)))
                    && rhs.serverRoot.equals(serverRoot)
                    && rhs.trackingRoot.equals(trackingRoot)
                    && rhs.isTrackingEnabled == isTrackingEnabled
                    && rhs.connectTimeout == connectTimeout
                    && rhs.requestTimeout == requestTimeout
                    && rhs.isApacheLoggingEnabled == isApacheLoggingEnabled
                    && rhs.showZeroSolutions == showZeroSolutions
                    && rhs.showRecurring == showRecurring;
        }
    }

    public Config getNetworkConfig() {
        final Config config = new Config();
        config.apiKey = apiKey;
        config.workspaceId = workspaceId;
        config.serverRoot = serverRoot;
        config.trackingRoot = trackingRoot;
        config.isTrackingEnabled = isTrackingEnabled;
        config.connectTimeout = connectTimeout;
        config.requestTimeout = requestTimeout;
        config.isApacheLoggingEnabled = isApacheLoggingEnabled;
        return config;
    }
}
