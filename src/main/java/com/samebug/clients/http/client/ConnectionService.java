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

import com.samebug.clients.http.response.ConnectionStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ConnectionService {
    protected AtomicBoolean connected;
    protected AtomicBoolean authenticated;
    protected AtomicInteger nRequests;
    protected AtomicReference<String> apiStatus;

    public ConnectionService() {
        this.connected = new AtomicBoolean(true);
        this.authenticated = new AtomicBoolean(true);
        this.nRequests = new AtomicInteger(0);
        this.apiStatus = new AtomicReference<String>(null);
    }

    public final boolean isConnected() {
        return connected.get();
    }

    public final boolean isAuthenticated() {
        return authenticated.get();
    }

    public final int getNumberOfActiveRequests() {
        return nRequests.get();
    }

    @Nullable
    public final String getApiStatus() {
        return apiStatus.get();
    }

    public final void beforeRequest() {
        nRequests.incrementAndGet();
        startRequest();
    }

    public final void afterRequest(ConnectionStatus status) {
        if (status.attemptToConnect) updateConnected(status.successfullyConnected);
        if (status.attemptToAuthenticate) updateAuthenticated(status.successfullyAuthenticated);
        if (status.apiStatus != null) updateApiStatus(status.apiStatus);
        nRequests.decrementAndGet();
        finishRequest(status);
    }

    void updateAuthenticated(boolean isAuthenticated) {
        if (authenticated.get() != isAuthenticated) {
            authenticated.set(isAuthenticated);
            authenticationChange(isAuthenticated);
        }
    }

    private void updateConnected(boolean isConnected) {
        if (connected.get() != isConnected) {
            connected.set(isConnected);
            connectionChange(isConnected);
        }
    }

    private void updateApiStatus(String apiStatus) {
        if (!apiStatus.equals(this.apiStatus.get())) {
            this.apiStatus.set(apiStatus);
            if (ConnectionStatus.API_TO_BE_DEPRECATED.equals(apiStatus)) {
                apiToBeDeprecated();
            } else if (ConnectionStatus.API_DEPRECATED.equals(apiStatus)) {
                apiDeprecated();
            }
        }
    }

    protected abstract void startRequest();

    protected abstract void finishRequest(ConnectionStatus status);

    /**
     * Called when the client becomes connected or disconnected
     * <p>
     * This is called only when there is a change.
     * Initially the client is supposed to be connected.
     *
     * @param isConnected current connection state
     */
    protected abstract void connectionChange(boolean isConnected);

    /**
     * Called when the client becomes authenticated or unauthenticated
     * <p>
     * This is called only when there is a change.
     * Initially the client is supposed to be authenticated.
     *
     * @param isAuthenticated current authentication state
     */
    protected abstract void authenticationChange(boolean isAuthenticated);

    /**
     * Called when the server sends to_be_deprecated status for the first time (resets when the service is configured)
     */
    protected abstract void apiToBeDeprecated();

    /**
     * Called when the server sends deprecated status for the first time (resets when the service is configured)
     */
    protected abstract void apiDeprecated();
}
