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
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.tracking.Solutions;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
import org.apache.http.*;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class SamebugClient {
    private final String apiKey;
    final static String USER_AGENT = "Samebug-Idea-Client/1.2.0";
    final static String API_VERSION = "2.0";
//    public final static URI root = URI.create("http://localhost:9000/");
     public final static URI root = URI.create("https://samebug.io/");
    final static URI trackingGateway = URI.create("http://nightly.samebug.com/").resolve("track/trace/");
    // final static URI trackingGateway = URI.create("https://samebug.io/").resolve("track/trace");
    final static URI gateway = root.resolve("sandbox/api/").resolve(API_VERSION + "/");
    final static Gson gson;

    // TODO is this a fine way of serialization of Date?
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                }
        );
        gson = builder.create();
    }

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

    public SearchResults searchSolutions(String stacktrace) throws SamebugClientException {
        List<NameValuePair> form = Form.form().add("exception", stacktrace).build();
        URL url = getApiUrl("search");
        Request post = Request.Post(url.toString());
        Request request = post.bodyForm(form, Consts.UTF_8);

        return requestJson(request, SearchResults.class);
    }

    public UserInfo getUserInfo(String apiKey) throws SamebugClientException {
        String url;
        try {
            url = getApiUrl("checkApiKey").toString() + "?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnableToPrepareUrl("Unable to resolve uri with apiKey " + apiKey, e);
        }
        Request request = Request.Get(url);

        return requestJson(request, UserInfo.class);
    }

    public GroupedHistory getSearchHistory() throws SamebugClientException {
        URL url = getApiUrl("history");
        Request request = Request.Get(url.toString());

        return requestJson(request, GroupedHistory.class);
    }

    public Solutions getSolutions(String searchId) throws SamebugClientException {
        URL url = getApiUrl("search/" + searchId);
        Request request = Request.Get(url.toString());

        return requestJson(request, Solutions.class);
    }

    public void trace(TrackEvent event) throws SamebugClientException {
        Request post = Request.Post(trackingGateway);
        postJson(post, event.fields);
    }

    // implementation
    private <T> T requestJson(Request request, final Class<T> classOfT)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        final HttpResponse httpResponse = executePatient(request.setHeader("Accept", "application/json"));
        return new HandleResponse<T>(httpResponse) {
            @Override
            T process(Reader reader) {
                return gson.fromJson(reader, classOfT);
            }
        }.handle();
    }

    private void postJson(Request post, Object data)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        String json = gson.toJson(data);
        post.addHeader("Content-Type", "application/json");
        post.body(new StringEntity(json, ContentType.APPLICATION_JSON));
        HttpResponse httpResponse = executeFailFast(post);

        new HandleResponse<Void>(httpResponse) {
            @Override
            Void process(Reader reader) {
                return null;
            }
        }.handle();
    }


    private HttpResponse executeFailFast(Request request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        return execute(request, 3000, 3000);
    }

    private HttpResponse executePatient(Request request)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        return execute(request, 3000, 7000);
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
     * @throws UserUnauthenticated        if the user was not authenticated (401)
     * @throws UserUnauthorized           if the user was not authorized (403)
     */
    private HttpResponse execute(Request request, int connectTimeoutMillis, int socketTimeoutMillis)
            throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthenticated, UserUnauthorized, HttpError {
        addDefaultHeaders(request);
        request.connectTimeout(connectTimeoutMillis);
        request.socketTimeout(socketTimeoutMillis);

        HttpResponse httpResponse;
        try {
            httpResponse = request.execute().returnResponse();
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

    private void addDefaultHeaders(Request request) {
        if (apiKey != null) request.addHeader("X-Samebug-ApiKey", apiKey);
        request.addHeader("User-Agent", USER_AGENT);
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