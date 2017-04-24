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

import com.samebug.clients.http.exceptions.*;
import com.samebug.clients.http.response.ConnectionStatus;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RawClient {
    static final String USER_AGENT = "Samebug-Idea-Client/2.0.0";
    static final int TrackingRequestTimeout_Millis = 3000;
    static final int MaxConnections = 20;

    final HttpClient httpClient;
    final Config config;
    final ConnectionService connectionService;
    final RequestConfig defaultRequestConfig;
    final RequestConfig trackingConfig;
    final RequestConfig.Builder requestConfigBuilder;

    public RawClient(@NotNull final Config config, @Nullable ConnectionService connectionService) {
        this.config = config;
        this.connectionService = connectionService;
        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        requestConfigBuilder = RequestConfig.custom();
        CredentialsProvider provider = new BasicCredentialsProvider();

        requestConfigBuilder.setConnectTimeout(config.connectTimeout).setSocketTimeout(config.requestTimeout).setConnectionRequestTimeout(500);
        final ProxyConfig proxySettings = config.proxy;
        if (proxySettings != null && proxySettings.host != null && !proxySettings.host.isEmpty()) {
            requestConfigBuilder.setProxy(new HttpHost(proxySettings.host, proxySettings.port));
            if (proxySettings.login != null && proxySettings.password != null) {
                provider.setCredentials(new AuthScope(proxySettings.host, proxySettings.port, AuthScope.ANY_REALM, AuthSchemes.NTLM),
                        new NTCredentials(proxySettings.login, proxySettings.password, null, null));
                provider.setCredentials(new AuthScope(proxySettings.host, proxySettings.port),
                        new UsernamePasswordCredentials(proxySettings.login, proxySettings.password));
            }
        }
        defaultRequestConfig = requestConfigBuilder.build();
        trackingConfig = requestConfigBuilder.setSocketTimeout(TrackingRequestTimeout_Millis).build();
        List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();
        defaultHeaders.add(new BasicHeader("User-Agent", USER_AGENT));

        httpClient = httpBuilder.setDefaultRequestConfig(defaultRequestConfig)
                .setMaxConnTotal(MaxConnections).setMaxConnPerRoute(MaxConnections)
                .setDefaultCredentialsProvider(provider)
                .setDefaultHeaders(defaultHeaders)
                .build();
    }

    public <T> T execute(final HandleRequest<T> handler) {
        final HttpRequestBase request = handler.createRequest();

        // initialize connection status
        ConnectionStatus connectionStatus;
        if (request.containsHeader("X-Samebug-ApiKey")) {
            connectionStatus = ConnectionStatus.authenticatedConnection();
            // NOTE if the api key was set, but it is null, we can tell that we will fail to authenticate without actually executing the request.
            if (request.getFirstHeader("X-Samebug-ApiKey").getValue() == null) return handler.onError(new UserUnauthenticated());
        } else {
            connectionStatus = ConnectionStatus.unauthenticatedConnection();
        }

        // execute request
        if (connectionService != null) connectionService.beforeRequest();
        try {
            final HttpResponse httpResponse = httpClient.execute(request);
            return processResponse(httpResponse, handler, connectionStatus);
        } catch (IOException e) {
            return handler.onError(new HttpError(e));
        } finally {
            if (connectionService != null) connectionService.afterRequest(connectionStatus);
        }
    }

    private <T> T processResponse(final HttpResponse httpResponse, final HandleRequest<T> handler, final ConnectionStatus connectionStatus) {
        final Header apiStatus = httpResponse.getFirstHeader("X-Samebug-ApiStatus");
        connectionStatus.apiStatus = apiStatus == null ? null : apiStatus.getValue();
        connectionStatus.successfullyConnected = true;

        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        switch (statusCode) {
            case HttpStatus.SC_OK:
                connectionStatus.successfullyAuthenticated = true;
                return handler.onSuccess(httpResponse);
            case HttpStatus.SC_BAD_REQUEST:
                connectionStatus.successfullyAuthenticated = true;
                return handler.onBadRequest(httpResponse);
            case HttpStatus.SC_UNAUTHORIZED:
                connectionStatus.successfullyAuthenticated = false;
                consumeEntity(httpResponse);
                return handler.onError(new UserUnauthenticated());
            case HttpStatus.SC_FORBIDDEN:
                connectionStatus.successfullyAuthenticated = true;
                consumeEntity(httpResponse);
                return handler.onError(new UserUnauthorized(httpResponse.getStatusLine().getReasonPhrase()));
            case HttpStatus.SC_GONE:
                connectionStatus.successfullyAuthenticated = true;
                connectionStatus.apiStatus = ConnectionStatus.API_DEPRECATED;
                consumeEntity(httpResponse);
                return handler.onError(new DeprecatedApiVersion());
            default:
                connectionStatus.successfullyAuthenticated = true;
                consumeEntity(httpResponse);
                return handler.onError(new UnsuccessfulResponseStatus(statusCode));
        }
    }

    public void executeTracking(final HttpRequestBase request) throws SamebugClientException {
        request.setConfig(trackingConfig);
        try {
            final HttpResponse httpResponse = httpClient.execute(request);
            consumeEntity(httpResponse);
        } catch (IOException e) {
            throw new HttpError(e);
        }
    }

    private void consumeEntity(HttpResponse httpResponse) {
        try {
            EntityUtils.consume(httpResponse.getEntity());
        } catch (IOException ignored) {
        }
    }
}

