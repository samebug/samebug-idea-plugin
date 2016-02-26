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
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
import org.apache.http.*;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class SamebugClient {
    private final String apiKey;
    private static final String USER_AGENT = "Samebug-Idea-Client/1.0.0";
    private static final String API_VERSION = "1.0";
        private final static URI root =  URI.create("http://localhost:9000/");
//    private final static URI root = URI.create("https://samebug.io/");
        private final static URI trackingGateway = URI.create("http://nightly.samebug.com/").resolve("track/trace/");
//    private final static URI trackingGateway = URI.create("https://samebug.io/").resolve("track/trace");
    private final static URI gateway = root.resolve("sandbox/api/").resolve(API_VERSION + "/");
    private static final Gson gson = new Gson();

    public SamebugClient(final String apiKey) {
        this.apiKey = apiKey;
    }

    public static URL getSearchUrl(int searchId) {
        String uri = "search/" + searchId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public static URL getUserProfileUrl(Integer userId) {
        String uri = "user/" + userId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public static URL getHistoryCssUrl(String themeId) {
        String uri = "assets-v/style/" + themeId + ".css";
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public SearchResults searchSolutions(String stacktrace)
            throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
        List<NameValuePair> form = Form.form().add("exception", stacktrace).build();
        URL url = getApiUrl("search");
        Request post = Request.Post(url.toString());
        Request request = post.bodyForm(form, Consts.UTF_8);

        return requestJson(request, SearchResults.class);
    }

    public UserInfo getUserInfo(String apiKey)
            throws RemoteError, UserUnauthorized, HttpError, SamebugTimeout, UnsuccessfulResponseStatus {
        String url;
        try {
            url = getApiUrl("checkApiKey").toString() + "?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("WTF");
        }
        Request request = Request.Get(url);

        return requestJson(request, UserInfo.class);
    }

    public History getSearchHistory(boolean recentFilterOn)
            throws RemoteError, UserUnauthorized, HttpError, SamebugTimeout, UnsuccessfulResponseStatus {
        // TODO use recentFilter
        URL url = getApiUrl("history");
        Request request = Request.Get(url.toString());

        return requestJson(request, History.class);
    }

    public void trace(TrackEvent event)
            throws RemoteError, UserUnauthorized, HttpError, SamebugTimeout, UnsuccessfulResponseStatus {
        Request post = Request.Post(trackingGateway);
        postJson(post, event.fields);
    }

    // implementation
    private <T> T requestJson(Request request, Class<T> classOfT)
            throws RemoteError, UserUnauthorized, UnsuccessfulResponseStatus, SamebugTimeout, HttpError {
        final HttpResponse httpResponse;
        httpResponse = executePatient(request.setHeader("Accept", "application/json"));

        Reader reader;
        try {
            reader = new InputStreamReader(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new HttpError(e);
        }
        try {
            return gson.fromJson(reader, classOfT);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void postJson(Request post, Object data)
            throws RemoteError, UserUnauthorized, UnsuccessfulResponseStatus, SamebugTimeout, HttpError {
        String json = gson.toJson(data);
        post.addHeader("Content-Type", "application/json");
        post.body(new StringEntity(json, ContentType.APPLICATION_JSON));
        executeFailFast(post);
    }


    private HttpResponse executeFailFast(Request request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthorized, HttpError {
        return execute(request, 3000, 3000);
    }

    private HttpResponse executePatient(Request request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthorized, HttpError {
        return execute(request, 10000, 30000);
    }


    /**
     * @param request              the http request
     * @param connectTimeoutMillis max milliseconds to wait to connect before failure
     * @param socketTimeoutMillis  max milliseconds to wait during I/O between packets before failure
     * @return the http response
     * @throws SamebugTimeout             if the server exceeded the timeout during connection or execute
     * @throws HttpError                  in case of a problem or the connection was aborted or   if the response is not readable
     * @throws UnsuccessfulResponseStatus if the response status is not 200
     * @throws RemoteError                if the server returned error in the X-Samebug-Errors header
     * @throws UserUnauthorized           if the user was not authorized
     */
    private HttpResponse execute(Request request, int connectTimeoutMillis, int socketTimeoutMillis)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthorized, HttpError {
        addDefaultHeaders(request);
        request.connectTimeout(connectTimeoutMillis);
        request.socketTimeout(socketTimeoutMillis);

        Response response;
        try {
            response = request.execute();
        } catch (IOException e) {
            throw new HttpError(e);
        }

        final HttpResponse httpResponse;
        try {
            httpResponse = response.returnResponse();
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
                throw new UserUnauthorized();
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

    private void addDefaultHeaders(Request request) {
        if (apiKey != null) request.addHeader("X-Samebug-ApiKey", apiKey);
        request.addHeader("User-Agent", USER_AGENT);
    }
}
