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
package com.samebug.clients.idea.components.application;

import com.intellij.util.net.HttpConfigurable;
import com.samebug.clients.http.client.Config;
import com.samebug.clients.http.client.ProxyConfig;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ApplicationSettings {
    //=========================================================================
    // NOTE: Make sure to extend equals and copy constructor when adding new fields!
    @Nullable
    public String apiKey;
    @Nullable
    public Integer workspaceId = defaultWorkspaceId;
    public String instanceId = UUID.randomUUID().toString();
    @Nullable
    public Integer userId;
    public String serverRoot = defaultServerRoot;
    public String trackingRoot = defaultTrackingRoot;
    public boolean isTrackingEnabled = defaultIsTrackingEnabled;
    public int connectTimeout = defaultConnectTimeout;
    public int requestTimeout = defaultRequestTimeout;
    public boolean isApacheLoggingEnabled = defaultIsApacheLoggingEnabled;
    public boolean isJsonDebugEnabled = defaultIsJsonDebugEnabled;
    public boolean isToolwindowDefaultModeOverridden = defaultIsToolwindowDefaultModeOverridden;

    //=========================================================================

    public static final Integer defaultWorkspaceId = null;
    public static final String defaultServerRoot = "https://samebug.io";
    public static final String defaultTrackingRoot = defaultServerRoot + "/track/trace";
    public static final boolean defaultIsTrackingEnabled = true;
    public static final int defaultConnectTimeout = 5000;
    public static final int defaultRequestTimeout = 10000;
    public static final boolean defaultIsApacheLoggingEnabled = false;
    public static final boolean defaultIsJsonDebugEnabled = false;
    public static final boolean defaultIsToolwindowDefaultModeOverridden = false;

    public ApplicationSettings() {
    }

    public ApplicationSettings(final ApplicationSettings rhs) {
        this.apiKey = rhs.apiKey;
        this.workspaceId = rhs.workspaceId;
        this.instanceId = rhs.instanceId;
        this.userId = rhs.userId;
        this.serverRoot = rhs.serverRoot;
        this.trackingRoot = rhs.trackingRoot;
        this.isTrackingEnabled = rhs.isTrackingEnabled;
        this.connectTimeout = rhs.connectTimeout;
        this.requestTimeout = rhs.requestTimeout;
        this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        this.isJsonDebugEnabled = rhs.isJsonDebugEnabled;
        this.isToolwindowDefaultModeOverridden = rhs.isToolwindowDefaultModeOverridden;
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
                    && ((rhs.userId == null && userId == null) || (rhs.userId != null && rhs.userId.equals(userId)))
                    && rhs.serverRoot.equals(serverRoot)
                    && rhs.trackingRoot.equals(trackingRoot)
                    && rhs.isTrackingEnabled == isTrackingEnabled
                    && rhs.connectTimeout == connectTimeout
                    && rhs.requestTimeout == requestTimeout
                    && rhs.isApacheLoggingEnabled == isApacheLoggingEnabled
                    && rhs.isJsonDebugEnabled == isJsonDebugEnabled
                    && rhs.isToolwindowDefaultModeOverridden == isToolwindowDefaultModeOverridden;
        }
    }

    public Config getNetworkConfig() {
        ProxyConfig proxyConfig;
        try {
            final HttpConfigurable iConfig = HttpConfigurable.getInstance();
            if (iConfig.isHttpProxyEnabledForUrl(serverRoot)) {
                proxyConfig = new ProxyConfig(iConfig.PROXY_HOST, iConfig.PROXY_PORT, iConfig.getProxyLogin(), iConfig.getPlainProxyPassword());
            } else {
                proxyConfig = null;
            }
        } catch (Exception ignored) {
            // if that fails, we pretend there is no proxy. This might fail do to subtle changes in the HttpConfigurable class between intellij versions.
            proxyConfig = null;
        }
        return new Config(apiKey, workspaceId, serverRoot,
                trackingRoot, isTrackingEnabled, connectTimeout, requestTimeout,
                isApacheLoggingEnabled, isJsonDebugEnabled, proxyConfig);
    }
}
