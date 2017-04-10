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
package com.samebug.clients.common.services;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.common.api.client.*;
import com.samebug.clients.common.api.entities.tracking.TrackEvent;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import org.apache.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NOTE: this service uses idea MessageBus only for convenience, any other observer implementation would do.
 */
public final class ClientService {
    final MessageBus messageBus;
    SamebugClient client;
    AtomicBoolean connected;
    AtomicBoolean authenticated;
    AtomicInteger nRequests;
    AtomicReference<String> apiStatus;

    public ClientService(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public synchronized void configure(final Config config) {
        this.client = new SamebugClient(config);
        // these parameters should keep the state of the previous config (if there was a previous config)
        this.connected = new AtomicBoolean(connected == null ? true : connected.get());
        this.authenticated = new AtomicBoolean(authenticated == null ? true : authenticated.get());
        this.nRequests = new AtomicInteger(nRequests == null ? 0 : nRequests.get());
        this.apiStatus = new AtomicReference<String>(apiStatus == null ? null : apiStatus.get());

        if (config.isApacheLoggingEnabled) enableApacheLogging();
    }

    public boolean isConnected() {
        return connected.get();
    }

    public boolean isAuthenticated() {
        return authenticated.get();
    }

    public int getNumberOfActiveRequests() {
        return nRequests.get();
    }

    @Nullable
    public String getApiStatus() {
        return apiStatus.get();
    }

    public void trace(final TrackEvent event) throws SamebugClientException {
        // Trace bypasses connection status handling.
        client.trace(event);
    }

    public <T> T execute(ConnectionAwareHttpRequest<T> c) throws SamebugClientException {
        final ClientResponse<T> response;

        c.start();
        nRequests.incrementAndGet();
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).startRequest();
        response = c.request();

        ConnectionStatus connectionStatus = response.getConnectionStatus();
        if (connectionStatus.attemptToConnect) updateConnected(connectionStatus.successfullyConnected);
        if (connectionStatus.attemptToAuthenticate) updateAuthenticated(connectionStatus.successfullyAuthenticated);
        if (connectionStatus.apiStatus != null) updateApiStatus(connectionStatus.apiStatus);
        try {
            if (response instanceof Success) {
                T result = ((Success<T>) response).getResponse();
                c.success(result);
                return result;
            } else {
                SamebugClientException exception = ((Failure<T>) response).getException();
                c.fail(exception);
                throw exception;
            }
        } finally {
            nRequests.decrementAndGet();
            messageBus.syncPublisher(ConnectionStatusListener.TOPIC).finishRequest(connectionStatus);
            c.finish(response);
        }
    }

    void updateAuthenticated(boolean isAuthenticated) {
        if (authenticated.get() != isAuthenticated) {
            authenticated.set(isAuthenticated);
            messageBus.syncPublisher(ConnectionStatusListener.TOPIC).authenticationChange(isAuthenticated);
        }
    }

    private void updateConnected(boolean isConnected) {
        if (connected.get() != isConnected) {
            connected.set(isConnected);
            messageBus.syncPublisher(ConnectionStatusListener.TOPIC).connectionChange(isConnected);
        }
    }

    private void updateApiStatus(String apiStatus) {
        if (!apiStatus.equals(this.apiStatus.get())) {
            this.apiStatus.set(apiStatus);
            if (ConnectionStatus.API_TO_BE_DEPRECATED.equals(apiStatus)) {
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).apiToBeDeprecated();
            } else if (ConnectionStatus.API_DEPRECATED.equals(apiStatus)) {
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).apiDeprecated();
            }
        }
    }

    public static abstract class ConnectionAwareHttpRequest<T> {
        abstract ClientResponse<T> request();

        protected void start() {
        }

        protected void success(T result) {
        }

        protected void fail(SamebugClientException e) {
        }

        protected void finish(ClientResponse<T> response) {
        }
    }


    public interface ConnectionStatusListener {
        Topic<ConnectionStatusListener> TOPIC = Topic.create("connection status change", ConnectionStatusListener.class);

        void startRequest();

        void finishRequest(ConnectionStatus status);

        /**
         * Called when the client becomes connected or disconnected
         * <p>
         * This is called only when there is a change.
         * Initially the client is supposed to be connected.
         *
         * @param isConnected current connection state
         */
        void connectionChange(boolean isConnected);

        /**
         * Called when the client becomes authenticated or unauthenticated
         * <p>
         * This is called only when there is a change.
         * Initially the client is supposed to be authenticated.
         *
         * @param isAuthenticated current authentication state
         */
        void authenticationChange(boolean isAuthenticated);

        /**
         * Called when the server sends to_be_deprecated status for the first time (resets when the service is configured)
         */
        void apiToBeDeprecated();

        /**
         * Called when the server sends deprecated status for the first time (resets when the service is configured)
         */
        void apiDeprecated();
    }

    private static void enableApacheLogging() {
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINER);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINER);
        Logger.getInstance("org.apache.http.conn.ssl.StrictHostnameVerifier").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.conn.DefaultManagedHttpClientConnection").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.conn.ssl.SSLConnectionSocketFactory").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.client.ProxyAuthenticationStrategy").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.client.DefaultRedirectStrategy").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.execchain.RetryExec").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.conn.DefaultHttpClientConnectionOperator").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.execchain.ProtocolExec").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.conn.ssl.DefaultHostnameVerifier").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.client.TargetAuthenticationStrategy").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.conn.DefaultHttpResponseParser").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.client.protocol.RequestAddCookies").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.conn.PoolingHttpClientConnectionManager").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.headers").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.auth.HttpAuthenticator").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.conn.ssl.BrowserCompatHostnameVerifier").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.execchain.MainClientExec").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.client.protocol.RequestAuthCache").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.wire").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.conn.CPool").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.conn.ssl.AllowAllHostnameVerifier").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.execchain.RedirectExec").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.impl.client.InternalHttpClient").setLevel(Level.DEBUG);
        Logger.getInstance("org.apache.http.client.protocol.RequestClientConnControl").setLevel(Level.DEBUG);
    }
}
