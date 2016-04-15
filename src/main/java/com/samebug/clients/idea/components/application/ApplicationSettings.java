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

import com.samebug.clients.search.api.SamebugClient;

import java.util.UUID;

/**
 * Created by poroszd on 2/12/16.
 */
public class ApplicationSettings {
    //=========================================================================
    // NOTE: Make sure to extend equals and copy constructor when adding new fields!
    public String apiKey;
    public String instanceId = UUID.randomUUID().toString();
    public int userId;
    public String serverRoot = defaultServerRoot;
    public String trackingRoot = defaultTrackingRoot;
    public boolean isTrackingEnabled = defaultIsTrackingEnabled;
    public int connectTimeout = defaultConnectTimeout;
    public int requestTimeout = defaultRequestTimeout;
    public boolean isApacheLoggingEnabled = defaultIsApacheLoggingEnabled;
    public boolean isWriteTipsEnabled = defaultIsWriteTipsEnabled;
    public boolean isMarkSolutionsEnabled = defaultIsMarkSolutionsEnabled;

    public boolean tutorialFirstRun = true;
    //=========================================================================

    public static final String defaultServerRoot = "https://samebug.io";
    public static final String defaultTrackingRoot = defaultServerRoot + "/track/trace";
    public static final boolean defaultIsTrackingEnabled = true;
    public static final int defaultConnectTimeout = 5000;
    public static final int defaultRequestTimeout = 10000;
    public static final boolean defaultIsApacheLoggingEnabled = false;
    public static final boolean defaultIsWriteTipsEnabled = false;
    public static final boolean defaultIsMarkSolutionsEnabled = false;

    public ApplicationSettings() {
    }

    public ApplicationSettings(final ApplicationSettings rhs) {
        this.apiKey = rhs.apiKey;
        this.instanceId = rhs.instanceId;
        this.userId = rhs.userId;
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        this.isWriteTipsEnabled = rhs.isWriteTipsEnabled;
        this.isMarkSolutionsEnabled = rhs.isMarkSolutionsEnabled;
        this.tutorialFirstRun = rhs.tutorialFirstRun;
    }

    @Override
    public int hashCode() {
        return ((31 + apiKey.hashCode()) * 31 + serverRoot.hashCode()) * 31 + trackingRoot.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof ApplicationSettings)) return false;
        else {
            final ApplicationSettings rhs = (ApplicationSettings) that;
            return rhs.apiKey.equals(apiKey)
                    && rhs.instanceId.equals(instanceId)
                    && rhs.userId == userId
                    && rhs.serverRoot.equals(serverRoot)
                    && rhs.trackingRoot.equals(trackingRoot)
                    && rhs.isTrackingEnabled == isTrackingEnabled
                    && rhs.connectTimeout == connectTimeout
                    && rhs.requestTimeout == requestTimeout
                    && rhs.isApacheLoggingEnabled == isApacheLoggingEnabled
                    && rhs.isWriteTipsEnabled == isWriteTipsEnabled
                    && rhs.isMarkSolutionsEnabled == isMarkSolutionsEnabled
                    && rhs.tutorialFirstRun == tutorialFirstRun;
        }
    }

    public SamebugClient.Config getNetworkConfig() {
        final SamebugClient.Config config = new SamebugClient.Config();
        config.apiKey = apiKey;
        config.serverRoot = serverRoot;
        config.trackingRoot = trackingRoot;
        config.isTrackingEnabled = isTrackingEnabled;
        config.connectTimeout = connectTimeout;
        config.requestTimeout = requestTimeout;
        config.isApacheLoggingEnabled = isApacheLoggingEnabled;
        return config;
    }
}
