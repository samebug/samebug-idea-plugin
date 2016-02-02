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
package com.samebug.clients.idea.application;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.exceptions.*;
import org.apache.http.*;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
public class IdeaSamebugClient implements SamebugClient, ApplicationComponent, PersistentStateComponent<SamebugSettings> {

    public void initIfNeeded() {
        if (!state.isInitialized()) ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SettingsDialog.setup(IdeaSamebugClient.this);
            }
        });
    }

    @NotNull
    public static IdeaSamebugClient getInstance() {
        IdeaSamebugClient instance = ApplicationManager.getApplication().getComponent(IdeaSamebugClient.class);
        if (instance == null) {
            throw new Error("No Samebug IDEA client available");
        } else {
            return instance;
        }
    }


    @Nullable
    public String getApiKey() {
        return state.getApiKey();
    }

    @Override
    public void initComponent() {
        initIfNeeded();
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    @Nullable
    @Override
    public SamebugSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(SamebugSettings state) {
        this.state = state;
    }

    public void setApiKey(String apiKey) throws SamebugClientException, UnknownApiKey {
        UserInfo userInfo = getUserInfo(apiKey);
        state.setApiKey(apiKey);
        state.setUserId(userInfo.userId);
        state.setUserDisplayName(userInfo.displayName);

    }

    public IdeaSamebugClient() {
        this.root = URI.create("https://samebug.io/");
        this.gateway = root.resolve("sandbox/api/").resolve(API_VERSION + "/");
    }

    @Override
    public SearchResults searchSolutions(String stacktrace) throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
        List<NameValuePair> form = Form.form().add("exception", stacktrace).build();
        URL url = getApiUrl("search");
        Request post = Request.Post(url.toString());
        Request request = post.bodyForm(form, Consts.UTF_8);

        return requestJson(request, SearchResults.class);
    }

    @Override
    public URL getSearchUrl(int searchId) {
        String uri = "search/" + searchId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }


    @Override
    public URL getUserProfileUrl(Integer userId) {
        String uri = "user/" + userId;
        try {
            return root.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    @Override
    public UserInfo getUserInfo(String apiKey) throws UnknownApiKey, SamebugClientException {
        String url;
        try {
            url = getApiUrl("checkApiKey").toString() + "?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("WTF");
        }
        Request request = Request.Get(url);

        UserInfo userInfo = requestJson(request, UserInfo.class);
        if (!userInfo.isUserExist) throw new UnknownApiKey(apiKey);
        return userInfo;
    }

    private <T> T requestJson(Request request, Class<T> classOfT) throws RemoteError, UserUnauthorized, UnsuccessfulResponseStatus, SamebugTimeout, HttpError {
        final HttpResponse httpResponse;
        httpResponse = execute(request.setHeader("Accept", "application/json"));

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

    /**
     * @param request the http request
     * @return the http response
     * @throws SamebugTimeout             if the server exceeded the timeout during connection or execure
     * @throws UnsuccessfulResponseStatus if the response status is not 200
     * @throws RemoteError                if the server returned error in the X-Samebug-Errors header
     * @throws UserUnauthorized           if the user was not authorized
     * @throws HttpError                  in case of a problem or the connection was aborted or   if the response is not readable
     */
    @NotNull
    private HttpResponse execute(Request request) throws SamebugTimeout, UnsuccessfulResponseStatus, RemoteError, UserUnauthorized, HttpError {
        addDefaultHeaders(request);
        request.connectTimeout(3000);
        request.socketTimeout(5000);

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
        String apiKey = getApiKey();
        if (apiKey != null) request.addHeader("X-Samebug-ApiKey", apiKey);
        request.addHeader("User-Agent", USER_AGENT);
    }

    private static final String USER_AGENT = "Samebug-Idea-Client/1.0.0";
    private static final String API_VERSION = "1.0";

    private SamebugSettings state = new SamebugSettings();
}

