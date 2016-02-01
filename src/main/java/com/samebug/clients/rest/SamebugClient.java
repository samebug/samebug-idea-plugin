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
package com.samebug.clients.rest;

import com.google.gson.Gson;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.entities.UserInfo;
import com.samebug.clients.rest.exceptions.*;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.conn.ConnectTimeoutException;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;

public class SamebugClient {
    public SamebugClient(SamebugIdeaPlugin plugin, URI root) {
        this.plugin = plugin;
        this.root = root;
        this.gateway = root.resolve("sandbox/api/").resolve(API_VERSION + "/");
    }

    public SearchResults searchSolutions(String stacktrace) throws SamebugClientException {
        List<NameValuePair> form = Form.form().add("exception", stacktrace).build();
        URL url = getApiUrl("search");
        Request post = Request.Post(url.toString());
        Request request = post.bodyForm(form, Consts.UTF_8);

        return requestJson(request, SearchResults.class);
    }

    public URL getSearchUrl(int searchId) {
        String uri = "search/" + searchId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }


    public URL getUserProfileUrl(Integer userId) {
        String uri = "user/" + userId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public UserInfo checkApiKey(String apiKey) throws SamebugClientException {
        String url;
        try {
            url = getApiUrl("checkApiKey").toString() + "?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("WTF");
        }
        Request request = Request.Get(url);

        return requestJson(request, UserInfo.class);
    }

    private <T> T requestJson(Request request, Class<T> classOfT) throws SamebugClientException {
        try {
            final HttpResponse httpResponse = execute(request.setHeader("Accept", "application/json"));
            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());
            try {
                return gson.fromJson(reader, classOfT);
            } finally {
                reader.close();
            }

        } catch (ClientProtocolException e) {
            throw new SamebugClientException("Unable to execute request", e);
        } catch (IOException e) {
            throw new SamebugClientException("Unable to execute request", e);
        }
    }

    @NotNull
    private HttpResponse execute(Request request) throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthorized, IOException {
        addDefaultHeaders(request);
        request.connectTimeout(3000);
        request.socketTimeout(5000);

        Response response;
        try {
            response = request.execute();

            final HttpResponse httpResponse = response.returnResponse();
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_OK:
                    final Header errors = httpResponse.getFirstHeader("X-Samebug-Errors");
                    if (errors != null) {
                        throw new RemoteError(errors.getValue());
                    }
                    return httpResponse;
                case HttpStatus.SC_UNAUTHORIZED:
                    throw new UserUnauthorized();
                default:
                    throw new UnsuccessfulResponseStatus(statusCode);
            }
        } catch (SocketTimeoutException e) {
            throw new SamebugSocketTimeout(e);
        } catch (ConnectTimeoutException e) {
            throw new SamebugConnectTimeout(e);
        } catch (IOException e) {
            throw e;
        }
    }

    private final SamebugIdeaPlugin plugin;
    private final URI root;
    private final URI gateway;
    private static final Gson gson = new Gson();

    private URL getApiUrl(String uri) throws SamebugClientError {
        URL url;
        try {
            url = gateway.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
        return url;
    }

    private void addDefaultHeaders(Request request) {
        String apiKey = plugin.getApiKey();
        if (apiKey != null) request.addHeader("X-Samebug-ApiKey", apiKey);
        request.addHeader("User-Agent", USER_AGENT);
    }

    private static final String USER_AGENT = "Samebug-Idea-Client/1.0.0";
    private static final String API_VERSION = "1.0";

}

