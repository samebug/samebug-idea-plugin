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

import java.net.URI;
import java.util.UUID;

/**
 * Created by poroszd on 2/12/16.
 */
public class ApplicationSettings {
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

    public static final String defaultServerRoot = "https://samebug.io/";
    public static final String defaultTrackingRoot = defaultServerRoot + "track/trace";
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
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        this.isWriteTipsEnabled = rhs.isWriteTipsEnabled;
        this.isMarkSolutionsEnabled = rhs.isMarkSolutionsEnabled;
        this.instanceId = rhs.instanceId;
        this.userId = rhs.userId;
        this.tutorialFirstRun = rhs.tutorialFirstRun;
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
