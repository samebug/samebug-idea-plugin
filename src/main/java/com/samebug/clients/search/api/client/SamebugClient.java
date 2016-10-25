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
package com.samebug.clients.search.api.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samebug.clients.search.api.RestUrlBuilder;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
import com.samebug.clients.search.api.json.Json;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final public class SamebugClient {
    final static Gson gson = Json.gson;
    public static final int TipSourceLoadingTime_Handicap_Millis = 30000;

    final Config config;
    final RestUrlBuilder urlBuilder;
    final RawClient rawClient;

    public SamebugClient(@NotNull final Config config) {
        this.config = new Config(config);
        this.urlBuilder = new RestUrlBuilder(config.serverRoot);
        this.rawClient = new RawClient(config);
    }

    public
    @NotNull
    ClientResponse<UserInfo> getUserInfo(@NotNull final String apiKey) {
        try {
            final URL url = urlBuilder.checkApiKey(apiKey);
            HttpGet request = new HttpGet(url.toString());

            return rawClient.execute(request, new HandleUnauthenticatedJsonRequest<UserInfo>(UserInfo.class));
        } catch (UnableToPrepareUrl unableToPrepareUrl) {
            ConnectionStatus connectionStatus = new ConnectionStatus();
            return new Failure<UserInfo>(connectionStatus, unableToPrepareUrl);
        }
    }

    public
    @NotNull
    ClientResponse<SearchResults> searchSolutions(@NotNull final String stacktrace) {
        final URL url = urlBuilder.search();
        HttpPost post = new HttpPost(url.toString());
        post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("exception", stacktrace)), Consts.UTF_8));

        return rawClient.execute(post, new HandleAuthenticatedJsonRequest<SearchResults>(SearchResults.class));
    }

    public
    @NotNull
    ClientResponse<SearchHistory> getSearchHistory() {
        final URL url = urlBuilder.history();
        HttpGet request = new HttpGet(url.toString());

        return rawClient.execute(request, new HandleAuthenticatedJsonRequest<SearchHistory>(SearchHistory.class));
    }

    public
    @NotNull
    ClientResponse<Solutions> getSolutions(@NotNull final Integer searchId) {
        final URL url = urlBuilder.search(searchId);
        HttpGet request = new HttpGet(url.toString());

        return rawClient.execute(request, new HandleAuthenticatedJsonRequest<Solutions>(Solutions.class));
    }

    public
    @NotNull
    ClientResponse<RestHit<Tip>> postTip(@NotNull final Integer searchId, @NotNull final String tip, @Nullable final String source) {
        final URL url = urlBuilder.tip();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
        // TODO checkstyle fails if there are only spaces before the next two lines
        if (tip != null) form.add(new BasicNameValuePair("message", tip));
        if (searchId != null) form.add(new BasicNameValuePair("searchId", searchId.toString()));
        if (source != null) form.add(new BasicNameValuePair("sourceUrl", source));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
        // NOTE: posting a tip includes downloading the source on the server side, which might take a while, hence we let it work a bit more.
        post.setConfig(rawClient.requestConfigBuilder.setSocketTimeout(config.requestTimeout + TipSourceLoadingTime_Handicap_Millis).build());
        Type typeToken = new TypeToken<RestHit<Tip>>() {
        }.getType();
        return rawClient.execute(post, new HandleAuthenticatedJsonRequest<RestHit<Tip>>(typeToken));
    }

    public
    @NotNull
    ClientResponse<MarkResponse> postMark(@NotNull final Integer searchId, @NotNull final Integer solutionId) {
        final URL url = urlBuilder.mark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Arrays.asList(new BasicNameValuePair("solution", solutionId.toString()),
                new BasicNameValuePair("search", searchId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return rawClient.execute(post, new HandleAuthenticatedJsonRequest<MarkResponse>(MarkResponse.class));
    }

    public
    @NotNull
    ClientResponse<MarkResponse> retractMark(@NotNull final Integer voteId) {
        final URL url = urlBuilder.cancelMark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Collections.singletonList(new BasicNameValuePair("mark", voteId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return rawClient.execute(post, new HandleAuthenticatedJsonRequest<MarkResponse>(MarkResponse.class));
    }

    public
    @NotNull
    ClientResponse<UserStats> getUserStats(@NotNull final Integer userId, @NotNull final Integer workspaceId) {
        try {
            UserStats s = gson.fromJson(new InputStreamReader(new FileInputStream("/home/poroszd/prg/samebug/samebug-idea-plugin/s.json")), UserStats.class);
            ConnectionStatus status = new ConnectionStatus();
            status.attemptToConnect = true;
            status.successfullyConnected = true;
            status.attemptToAuthenticate = true;
            status.successfullyAuthenticated = true;
            return new Success<UserStats>(status, s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

//        final URL url = urlBuilder.userStats(userId, workspaceId);
//        HttpGet get = new HttpGet(url.toString());
//
//        return rawClient.execute(get, new HandleAuthenticatedJsonRequest<UserStats>(UserStats.class));
    }

    public void trace(@NotNull final TrackEvent event) throws SamebugClientException {
        if (config.isTrackingEnabled) {
            HttpPost post = new HttpPost(config.trackingRoot);
            post.addHeader("Content-Type", "application/json");
            final String json = gson.toJson(event.fields);
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            rawClient.executeTracking(post);
        }
    }

    final class HandleAuthenticatedJsonRequest<T> extends HandleJsonRequest<T> {
        HandleAuthenticatedJsonRequest(Type classOfT) {
            super(classOfT);
        }

        @Override
        boolean isAuthenticated() {
            return true;
        }
    }

    final class HandleUnauthenticatedJsonRequest<T> extends HandleJsonRequest<T> {
        HandleUnauthenticatedJsonRequest(Type classOfT) {
            super(classOfT);
        }

        @Override
        boolean isAuthenticated() {
            return false;
        }
    }

    abstract class HandleJsonRequest<T> extends HandleRequest<T> {
        private Type classOfT;

        HandleJsonRequest(Type classOfT) {
            this.classOfT = classOfT;
        }

        @Override
        T onSuccess(HttpResponse response) throws ProcessResponseException {
            return readJsonResponse(response, classOfT);
        }

        @Override
        RestError onBadRequest(HttpResponse response) throws ProcessResponseException {
            return readJsonResponse(response, RestError.class);
        }

        @Override
        void modifyRequest(HttpRequestBase request) {
            request.setHeader("Accept", "application/json");
        }
    }

    <T> T readJsonResponse(HttpResponse response, Type classOfT) throws HttpError, JsonParseException {
        InputStream content = null;
        Reader reader = null;
        try {
            content = response.getEntity().getContent();
            reader = new InputStreamReader(content);
            return gson.fromJson(reader, classOfT);
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
