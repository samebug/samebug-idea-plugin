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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.IdeHttpClientHelpers;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Solutions;
import com.samebug.clients.search.api.entities.legacy.Tip;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
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
import org.apache.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SamebugClient {
    final static String USER_AGENT = "Samebug-Idea-Client/1.3.0";
    final static String API_VERSION = "0.8";
    final static Gson gson;

    final Config config;
    final URI gateway;
    final HttpClient httpClient;
    final RequestConfig requestConfig;
    final RequestConfig trackingConfig;


    static {
        // TODO is this a fine way of serialization of Date?
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                }
        );
        gson = gsonBuilder.create();
    }

    public SamebugClient(final Config config) {
        this.config = new Config(config);
        this.gateway = config.serverRoot.resolve("rest/").resolve(API_VERSION + "/");
        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        CredentialsProvider provider = new BasicCredentialsProvider();

        requestConfigBuilder.setConnectTimeout(config.connectTimeout).setSocketTimeout(config.requestTimeout).setConnectionRequestTimeout(500);
        try {
            IdeHttpClientHelpers.ApacheHttpClient4.setProxyForUrlIfEnabled(requestConfigBuilder, config.serverRoot.toString());
            IdeHttpClientHelpers.ApacheHttpClient4.setProxyCredentialsForUrlIfEnabled(provider, config.serverRoot.toString());
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
            } catch (Throwable e1) {
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

    public SearchResults searchSolutions(String stacktrace) throws SamebugClientException {
        URL url = getApiUrl("search");
        HttpPost post = new HttpPost(url.toString());
        post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("exception", stacktrace)), Consts.UTF_8));
        return requestJson(post, SearchResults.class);
    }

    public UserInfo getUserInfo(String apiKey) throws SamebugClientException {
        String url;
        try {
            url = getApiUrl("checkApiKey").toString() + "?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnableToPrepareUrl("Unable to resolve uri with apiKey " + apiKey, e);
        }
        HttpGet request = new HttpGet(url);

        return requestJson(request, UserInfo.class);
    }

    public GroupedHistory getSearchHistory() throws SamebugClientException {
        URL url = getApiUrl("history");
        HttpGet request = new HttpGet(url.toString());

        return requestJson(request, GroupedHistory.class);
    }

    public Solutions getSolutions(int searchId) throws SamebugClientException {
        URL url = getApiUrl("search/" + searchId);
        HttpGet request = new HttpGet(url.toString());

        return requestJson(request, Solutions.class);
    }

    public RestHit<Tip> postTip(int searchId, String tip, URL source) throws SamebugClientException {
        // TODO implement
        try {
            Thread.sleep(3000);
            if (Math.random() > 0.5) {
                return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/com/samebug/mock/tip.json")), new TypeToken<RestHit<Tip>>() {
                }.getType());
            } else {
                throw new SamebugClientException("Server is down");
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    public MarkResponse postMark(int searchId, int solutionId) throws SamebugClientException {
        // TODO implement
        try {
            Thread.sleep(3000);
            if (Math.random() > 0.5) {
                return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/com/samebug/mock/markResponse.json")), MarkResponse.class);
            } else {
                throw new SamebugClientException("Server is down");
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    public MarkResponse retractMark(int voteId) throws SamebugClientException {
        // TODO implement
        try {
            Thread.sleep(3000);
            if (Math.random() > 0.5) {
                return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/com/samebug/mock/markResponse.json")), MarkResponse.class);
            } else {
                throw new SamebugClientException("Server is down");
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void trace(TrackEvent event) throws SamebugClientException {
        if (config.isTrackingEnabled) {
            HttpPost post = new HttpPost(config.trackingRoot);
            postJson(post, event.fields);
        }
    }

    // implementation
    private <T> T requestJson(HttpRequestBase request, final Class<T> classOfT)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        request.setHeader("Accept", "application/json");
        final HttpResponse httpResponse = executePatient(request);
        return new HandleResponse<T>(httpResponse) {
            @Override
            T process(Reader reader) {
                return gson.fromJson(reader, classOfT);
            }
        }.handle();
    }

    private void postJson(HttpPost post, Object data)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        String json = gson.toJson(data);
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        HttpResponse httpResponse = executeFailFast(post);

        new HandleResponse<Void>(httpResponse) {
            @Override
            Void process(Reader reader) {
                return null;
            }
        }.handle();
    }


    private HttpResponse executeFailFast(HttpRequestBase request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        return execute(request, trackingConfig);
    }

    private HttpResponse executePatient(HttpRequestBase request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        return execute(request, null);
    }


    /**
     * @param request the http request
     * @return the http response
     * @throws SamebugTimeout             if the server exceeded the timeout during connection or execute
     * @throws HttpError                  in case of a problem or the connection was aborted or   if the response is not readable
     * @throws UnsuccessfulResponseStatus if the response status is not 200
     * @throws RemoteError                if the server returned error in the X-Samebug-Errors header
     * @throws UserUnauthenticated        if the user was not authenticated (401)
     * @throws UserUnauthorized           if the user was not authorized (403)
     */
    private HttpResponse execute(HttpRequestBase request, @Nullable RequestConfig config)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        if (config != null) request.setConfig(config);

        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new HttpError(e);
        }

        int statusCode = httpResponse.getStatusLine().getStatusCode();

        switch (statusCode) {
            case HttpStatus.SC_OK:
                final Header errors = httpResponse.getFirstHeader("X-Samebug-Errors");
                if (errors != null) {
                    throw new RemoteError(errors.getValue());
                }
                return httpResponse;
            case HttpStatus.SC_UNAUTHORIZED:
                throw new UserUnauthenticated();
            case HttpStatus.SC_FORBIDDEN:
                throw new UserUnauthorized(httpResponse.getStatusLine().getReasonPhrase());
            default:
                throw new UnsuccessfulResponseStatus(statusCode);
        }
    }

    private URL getApiUrl(String uri) throws SamebugClientError {
        URL url;
        try {
            url = gateway.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
        return url;
    }

    public static class Config {
        public String apiKey;
        public URI serverRoot;
        public URI trackingRoot;
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
    final HttpResponse response;

    HandleResponse(HttpResponse response) {
        this.response = response;
    }

    abstract T process(Reader reader);

    public T handle() throws HttpError {
        InputStream content = null;
        Reader reader = null;
        try {
            content = response.getEntity().getContent();
            reader = new InputStreamReader(content);
            return process(reader);
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
