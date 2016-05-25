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
package com.samebug.clients.search.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.IdeHttpClientHelpers;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
import com.samebug.clients.search.api.json.Json;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final public class SamebugClient {
    final static String USER_AGENT = "Samebug-Idea-Client/2.0.0";
    final static Gson gson = Json.gson;

    final Config config;
    final HttpClient httpClient;
    final RequestConfig requestConfig;
    final RequestConfig trackingConfig;
    final RestUrlBuilder urlBuilder;

    public SamebugClient(@NotNull final Config config) {
        this.config = new Config(config);
        this.urlBuilder = new RestUrlBuilder(config.serverRoot);

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        CredentialsProvider provider = new BasicCredentialsProvider();

        requestConfigBuilder.setConnectTimeout(config.connectTimeout).setSocketTimeout(config.requestTimeout).setConnectionRequestTimeout(500);
        try {
            IdeHttpClientHelpers.ApacheHttpClient4.setProxyForUrlIfEnabled(requestConfigBuilder, config.serverRoot);
            IdeHttpClientHelpers.ApacheHttpClient4.setProxyCredentialsForUrlIfEnabled(provider, config.serverRoot);
        } catch (Throwable e) {
            // fallback to traditional proxy config for backward compatiblity
            try {
                final HttpConfigurable proxySettings = HttpConfigurable.getInstance();
                if (proxySettings != null && proxySettings.USE_HTTP_PROXY && !StringUtil.isEmptyOrSpaces(proxySettings.PROXY_HOST)) {
                    requestConfigBuilder.setProxy(new HttpHost(proxySettings.PROXY_HOST, proxySettings.PROXY_PORT));
                    if (proxySettings.PROXY_AUTHENTICATION) {
                        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxySettings.PROXY_LOGIN, proxySettings.getPlainProxyPassword()));
                    }
                }
            } catch (Throwable ignored) {
                // if even that fails, we cannot do more
            }
        }
        requestConfig = requestConfigBuilder.build();
        trackingConfig = requestConfigBuilder.setSocketTimeout(3000).build();
        List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();
        defaultHeaders.add(new BasicHeader("User-Agent", USER_AGENT));
        if (config.apiKey != null) defaultHeaders.add(new BasicHeader("X-Samebug-ApiKey", config.apiKey));

        httpClient = httpBuilder.setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(20).setMaxConnPerRoute(20)
                .setDefaultCredentialsProvider(provider)
                .setDefaultHeaders(defaultHeaders)
                .build();

        if (config.isApacheLoggingEnabled) enableApacheLogging();
    }

    public
    @NotNull
    SearchResults searchSolutions(@NotNull final String stacktrace) throws SamebugClientException {
        final URL url = urlBuilder.search();
        HttpPost post = new HttpPost(url.toString());
        post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("exception", stacktrace)), Consts.UTF_8));
        return requestJson(post, SearchResults.class);
    }

    public
    @NotNull
    UserInfo getUserInfo(@NotNull final String apiKey) throws SamebugClientException {
        final URL url = urlBuilder.checkApiKey(apiKey);
        HttpGet request = new HttpGet(url.toString());

        return requestJson(request, UserInfo.class);
    }

    public
    @NotNull
    SearchHistory getSearchHistory() throws SamebugClientException {
        final URL url = urlBuilder.history();
        HttpGet request = new HttpGet(url.toString());

        return requestJson(request, SearchHistory.class);
    }

    public
    @NotNull
    Solutions getSolutions(@NotNull final Integer searchId) throws SamebugClientException {
        final URL url = urlBuilder.search(searchId);
        HttpGet request = new HttpGet(url.toString());

        return requestJson(request, Solutions.class);
    }

    public
    @NotNull
    RestHit<Tip> postTip(@NotNull final Integer searchId, @NotNull final String tip, @Nullable final URL source) throws SamebugClientException {
        final URL url = urlBuilder.tip();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
        // TODO checkstyle fails if there are only spaces before the next two lines
        if (tip != null) form.add(new BasicNameValuePair("message", tip));
        if (searchId != null) form.add(new BasicNameValuePair("searchId", searchId.toString()));
        if (source != null) form.add(new BasicNameValuePair("sourceUrl", source.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return requestJson(post, new TypeToken<RestHit<Tip>>() {
        }.getType());
    }

    public
    @NotNull
    MarkResponse postMark(@NotNull final Integer searchId, @NotNull final Integer solutionId) throws SamebugClientException {
        final URL url = urlBuilder.mark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Arrays.asList(new BasicNameValuePair("solution", solutionId.toString()),
                new BasicNameValuePair("search", searchId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return requestJson(post, MarkResponse.class);
    }

    public
    @NotNull
    MarkResponse retractMark(@NotNull final Integer voteId) throws SamebugClientException {
        final URL url = urlBuilder.cancelMark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Collections.singletonList(new BasicNameValuePair("mark", voteId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return requestJson(post, MarkResponse.class);
    }

    public void trace(@NotNull final TrackEvent event) throws SamebugClientException {
        if (config.isTrackingEnabled) {
            HttpPost post = new HttpPost(config.trackingRoot);
            postJson(post, event.fields);
        }
    }

    // implementation
    <T> T requestJson(final HttpRequestBase request, final Class<T> classOfT)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        return requestJson(request, (Type) classOfT);
    }

    <T> T requestJson(final HttpRequestBase request, final Type typeOfT)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        request.setHeader("Accept", "application/json");
        final HttpResponse httpResponse = executePatient(request);
        return new HandleResponse<T>(httpResponse) {
            @Override
            T process(final Reader reader) {
                return gson.fromJson(reader, typeOfT);
            }
        }.handle();
    }

    private void postJson(final HttpPost post, final Object data)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        final String json = gson.toJson(data);
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        final HttpResponse httpResponse = executeFailFast(post);

        new HandleResponse<Void>(httpResponse) {
            @Override
            Void process(final Reader reader) {
                return null;
            }
        }.handle();
    }


    private HttpResponse executeFailFast(final HttpRequestBase request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        return execute(request, trackingConfig);
    }

    private HttpResponse executePatient(final HttpRequestBase request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        return execute(request, null);
    }


    /**
     * @param request the http request
     * @return the http response
     * @throws SamebugTimeout             if the server exceeded the timeout during connection or execute
     * @throws HttpError                  in case of a problem or the connection was aborted or   if the response is not readable
     * @throws UnsuccessfulResponseStatus if the response status is not 200
     * @throws RemoteError                if the server returned error in the X-Samebug-Errors header
     * @throws BadRequest                 if the client state is inconsistent with the server (400)
     * @throws UserUnauthenticated        if the user was not authenticated (401)
     * @throws UserUnauthorized           if the user was not authorized (403)
     */
    private HttpResponse execute(final HttpRequestBase request, final RequestConfig config)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, BadRequest, UserUnauthenticated, UserUnauthorized, HttpError, JsonParseException {
        if (config != null) request.setConfig(config);

        final HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new HttpError(e);
        }

        final int statusCode = httpResponse.getStatusLine().getStatusCode();

        switch (statusCode) {
            case HttpStatus.SC_OK:
                final Header errors = httpResponse.getFirstHeader("X-Samebug-Errors");
                if (errors != null) {
                    throw new RemoteError(errors.getValue());
                }
                return httpResponse;
            case HttpStatus.SC_BAD_REQUEST:
                final RestError restError = new HandleResponse<RestError>(httpResponse) {
                    @Override
                    RestError process(final Reader reader) {
                        return gson.fromJson(reader, RestError.class);
                    }
                }.handle();
                throw new BadRequest(restError);
            case HttpStatus.SC_UNAUTHORIZED:
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                throw new UserUnauthenticated();
            case HttpStatus.SC_FORBIDDEN:
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                throw new UserUnauthorized(httpResponse.getStatusLine().getReasonPhrase());
            default:
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException ignored) {
                }
                throw new UnsuccessfulResponseStatus(statusCode);
        }
    }

    public static class Config {
        public String apiKey;
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
            this.serverRoot = rhs.serverRoot;
            this.trackingRoot = rhs.trackingRoot;
            this.isTrackingEnabled = rhs.isTrackingEnabled;
            this.connectTimeout = rhs.connectTimeout;
            this.requestTimeout = rhs.requestTimeout;
            this.isApacheLoggingEnabled = rhs.isApacheLoggingEnabled;
        }
    }

    static void enableApacheLogging() {
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


abstract class HandleResponse<T> {
    HttpResponse response;

    HandleResponse(final HttpResponse response) {
        this.response = response;
    }

    abstract T process(final Reader reader);

    final public T handle() throws HttpError, JsonParseException {
        InputStream content = null;
        Reader reader = null;
        try {
            content = response.getEntity().getContent();
            reader = new InputStreamReader(content);
            return process(reader);
        } catch (com.google.gson.JsonParseException e) {
            throw new JsonParseException("Failed to parse json response", e);
        } catch (IOException e) {
            throw new HttpError(e);
        } finally {
            try {
                if (content != null) content.close();
                if (reader != null) reader.close();
            } catch (IOException ignored) {
            }
        }
    }
}
