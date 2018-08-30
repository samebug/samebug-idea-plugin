/*
 * Copyright 2018 Samebug, Inc.
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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.http.client.Config;
import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.idea.controllers.NotificationController;
import com.samebug.clients.idea.controllers.WebSocketClientService;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;

public final class IdeaClientService implements ClientService, Disposable {
    SamebugClient client;
    WebSocketClientService wsClient;
    IdeaConnectionService connectionService;

    public IdeaClientService(MessageBus messageBus) {
        this.connectionService = new IdeaConnectionService(messageBus);
        this.wsClient = new WebSocketClientService(new NotificationController());
    }

    public void configure(final Config config) {
        this.client = new SamebugClient(config, connectionService);
        this.wsClient.configure(config);

        if (config.isApacheLoggingEnabled) enableApacheLogging();
    }

    @NotNull
    public SamebugClient getClient() {
        return client;
    }

    public WebSocketClientService getWsClient() {
        return wsClient;
    }

    public IdeaConnectionService getConnectionService() {
        return connectionService;
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

    @Override
    public void dispose() {
        wsClient.dispose();
    }
}
