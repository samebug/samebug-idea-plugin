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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samebug.clients.common.api.RestUrlBuilder;
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.entities.UserStats;
import com.samebug.clients.common.api.entities.bugmate.BugmatesResult;
import com.samebug.clients.common.api.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.entities.search.CreatedSearch;
import com.samebug.clients.common.api.entities.search.SearchDetails;
import com.samebug.clients.common.api.entities.solution.MarkResponse;
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Solutions;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.entities.tracking.TrackEvent;
import com.samebug.clients.common.api.exceptions.*;
import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.api.json.Json;
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

            return rawClient.executeUnauthenticated(request, new HandleJsonRequest<UserInfo>(UserInfo.class));
        } catch (UnableToPrepareUrl unableToPrepareUrl) {
            ConnectionStatus connectionStatus = new ConnectionStatus();
            return new Failure<UserInfo>(connectionStatus, unableToPrepareUrl);
        }
    }

    public
    @NotNull
    ClientResponse<CreatedSearch> createSearch(@NotNull final String stacktrace) {
        final URL url = urlBuilder.search();
        HttpPost post = new HttpPost(url.toString());
        post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("exception", stacktrace)), Consts.UTF_8));

        return rawClient.executeAuthenticated(post, new HandleJsonRequest<CreatedSearch>(CreatedSearch.class));
    }

    public
    @NotNull
    ClientResponse<SearchDetails> getSearch(@NotNull final Integer searchId) {
        final URL url = urlBuilder.search(searchId);
        HttpGet request = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<SearchDetails>(SearchDetails.class));
    }

    public
    @NotNull
    ClientResponse<Solutions> getSolutions(@NotNull final Integer searchId) {
        final URL url = urlBuilder.solutions(searchId);
        HttpGet request = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<Solutions>(Solutions.class));
    }

    public
    @NotNull
    ClientResponse<BugmatesResult> getBugmates(@NotNull final Integer searchId) {
        final URL url = urlBuilder.bugmates(searchId);
        HttpGet request = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<BugmatesResult>(BugmatesResult.class));
    }

    public
    @NotNull
    ClientResponse<IncomingHelpRequests> getIncomingHelpRequests() {
        final URL url = urlBuilder.incomingHelpRequests();
        HttpGet request = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<IncomingHelpRequests>(IncomingHelpRequests.class));
    }

    public
    @NotNull
    ClientResponse<MatchingHelpRequest> getHelpRequest(String helpRequestId) {
        final URL url = urlBuilder.getHelpRequest(helpRequestId);
        HttpGet request = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<MatchingHelpRequest>(MatchingHelpRequest.class));
    }

    // TODO use the api.form classes here
    public
    @NotNull
    ClientResponse<MyHelpRequest> createHelpRequest(int searchId, String context) {
        final URL url = urlBuilder.helpRequest();
        HttpPost request = new HttpPost(url.toString());
        List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
        form.add(new BasicNameValuePair(CreateHelpRequest.SEARCH_ID, Integer.toString(searchId)));
        form.add(new BasicNameValuePair(CreateHelpRequest.CONTEXT, context));
        request.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<MyHelpRequest>(MyHelpRequest.class));
    }

    public
    @NotNull
    ClientResponse<MyHelpRequest> revokeHelpRequest(String helpRequestId) {
        final URL url = urlBuilder.revokeHelpRequest(helpRequestId);
        HttpPost request = new HttpPost(url.toString());

        return rawClient.executeAuthenticated(request, new HandleJsonRequest<MyHelpRequest>(MyHelpRequest.class));
    }

    public
    @NotNull
    ClientResponse<RestHit<Tip>> createTip(@NotNull final Integer searchId, @NotNull final String tip, @Nullable final String source, @Nullable String helpRequestId) {
        final URL url = urlBuilder.tip();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
        form.add(new BasicNameValuePair("message", tip));
        form.add(new BasicNameValuePair("searchId", searchId.toString()));
        if (source != null) form.add(new BasicNameValuePair("sourceUrl", source));
        if (helpRequestId != null) form.add(new BasicNameValuePair("helpRequestId", helpRequestId));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
        // NOTE: posting a tip includes downloading the source on the server side, which might take a while, hence we let it work a bit more.
        post.setConfig(rawClient.requestConfigBuilder.setSocketTimeout(config.requestTimeout + TipSourceLoadingTime_Handicap_Millis).build());
        Type typeToken = new TypeToken<RestHit<Tip>>() {
        }.getType();
        return rawClient.executeAuthenticated(post, new HandleJsonRequest<RestHit<Tip>>(typeToken));
    }

    public
    @NotNull
    ClientResponse<MarkResponse> postMark(@NotNull final Integer searchId, @NotNull final Integer solutionId) {
        final URL url = urlBuilder.mark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Arrays.asList(new BasicNameValuePair("solution", solutionId.toString()),
                new BasicNameValuePair("search", searchId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return rawClient.executeAuthenticated(post, new HandleJsonRequest<MarkResponse>(MarkResponse.class));
    }

    public
    @NotNull
    ClientResponse<MarkResponse> retractMark(@NotNull final Integer voteId) {
        final URL url = urlBuilder.cancelMark();
        HttpPost post = new HttpPost(url.toString());
        List<BasicNameValuePair> form = Collections.singletonList(new BasicNameValuePair("mark", voteId.toString()));
        post.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

        return rawClient.executeAuthenticated(post, new HandleJsonRequest<MarkResponse>(MarkResponse.class));
    }

    public
    @NotNull
    ClientResponse<UserStats> getUserStats() {
        final URL url = urlBuilder.userStats();
        HttpGet get = new HttpGet(url.toString());

        return rawClient.executeAuthenticated(get, new HandleJsonRequest<UserStats>(UserStats.class));
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

    final class HandleJsonRequest<T> extends HandleRequest<T> {
        private Type classOfT;

        HandleJsonRequest(Type classOfT) {
            this.classOfT = classOfT;
        }

        @Override
        T onSuccess(HttpResponse response) throws ProcessResponseException {
            return readJsonResponse(response, classOfT);
        }

        @Override
        BasicRestError onBadRequest(HttpResponse response) throws ProcessResponseException {
            return readJsonResponse(response, BasicRestError.class);
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
