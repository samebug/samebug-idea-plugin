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
package com.samebug.clients.common.api.client;

import com.samebug.clients.common.api.exceptions.*;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class RawClient {
    final static String USER_AGENT = "Samebug-Idea-Client/2.0.0";
    public static final int TrackingRequestTimeout_Millis = 3000;
    public static final int MaxConnections = 20;

    final HttpClient httpClient;
    final RequestConfig defaultRequestConfig;
    final RequestConfig trackingConfig;
    final RequestConfig.Builder requestConfigBuilder;

    RawClient(@NotNull final Config config) {
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
        // NOTE identification headers are always posted. They are necessary only for authenticated requests, but probably doesn't hurt to post anyway, and it's simpler this way
        if (config.apiKey != null) defaultHeaders.add(new BasicHeader("X-Samebug-ApiKey", config.apiKey));
        if (config.workspaceId != null) defaultHeaders.add(new BasicHeader("X-Samebug-WorkspaceId", config.workspaceId.toString()));

        httpClient = httpBuilder.setDefaultRequestConfig(defaultRequestConfig)
                .setMaxConnTotal(MaxConnections).setMaxConnPerRoute(MaxConnections)
                .setDefaultCredentialsProvider(provider)
                .setDefaultHeaders(defaultHeaders)
                .build();
    }

    <T> ClientResponse<T> executeAuthenticated(final HttpRequestBase request, final HandleRequest<T> handler) {
        ConnectionStatus connectionStatus = ConnectionStatus.authenticatedConnection();
        return executeRequest(request, handler, connectionStatus);
    }

    <T> ClientResponse<T> executeUnauthenticated(final HttpRequestBase request, final HandleRequest<T> handler) {
        ConnectionStatus connectionStatus = ConnectionStatus.unauthenticatedConnection();
        return executeRequest(request, handler, connectionStatus);
    }

    private <T> ClientResponse<T> executeRequest(final HttpRequestBase request, final HandleRequest<T> handler, final ConnectionStatus connectionStatus) {
        handler.modifyRequest(request);
        final HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            return new Failure<T>(connectionStatus, new HttpError(e));
        }
        return processResponse(httpResponse, handler, connectionStatus);
    }

    private <T> ClientResponse<T> processResponse(final HttpResponse httpResponse, final HandleRequest<T> handler, final ConnectionStatus connectionStatus) {
        final Header apiStatus = httpResponse.getFirstHeader("X-Samebug-ApiStatus");
        connectionStatus.apiStatus = apiStatus == null ? null : apiStatus.getValue();
        connectionStatus.successfullyConnected = true;

        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        switch (statusCode) {
            case HttpStatus.SC_OK:
                connectionStatus.successfullyAuthenticated = true;
                try {
                    T response = handler.onSuccess(httpResponse);
                    return new Success<T>(connectionStatus, response);
                } catch (ProcessResponseException e) {
                    return new Failure<T>(connectionStatus, e);
                }
            case HttpStatus.SC_BAD_REQUEST:
                connectionStatus.successfullyAuthenticated = true;
                try {
                    BasicRestError processedError = handler.onBadRequest(httpResponse);
                    return new Failure<T>(connectionStatus, new BadRequest(processedError));
                } catch (ProcessResponseException e) {
                    return new Failure<T>(connectionStatus, e);
                }
            case HttpStatus.SC_UNAUTHORIZED:
                connectionStatus.successfullyAuthenticated = false;
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                return new Failure<T>(connectionStatus, new UserUnauthenticated());
            case HttpStatus.SC_FORBIDDEN:
                connectionStatus.successfullyAuthenticated = true;
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                return new Failure<T>(connectionStatus, new UserUnauthorized(httpResponse.getStatusLine().getReasonPhrase()));
            case HttpStatus.SC_GONE:
                connectionStatus.successfullyAuthenticated = true;
                connectionStatus.apiStatus = ConnectionStatus.API_DEPRECATED;
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                return new Failure<T>(connectionStatus, new DeprecatedApiVersion());
            default:
                connectionStatus.successfullyAuthenticated = true;
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                return new Failure<T>(connectionStatus, new UnsuccessfulResponseStatus(statusCode));
        }
    }

    void executeTracking(final HttpRequestBase request) throws SamebugClientException {
        final HttpResponse httpResponse;
        request.setConfig(trackingConfig);
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new HttpError(e);
        }

        try {
            EntityUtils.consume(httpResponse.getEntity());
        } catch (IOException ignored) {
        }
    }
}

abstract class HandleRequest<T> {
    abstract T onSuccess(final HttpResponse response) throws ProcessResponseException;

    abstract BasicRestError onBadRequest(final HttpResponse response) throws ProcessResponseException;

    abstract void modifyRequest(HttpRequestBase request);

}
