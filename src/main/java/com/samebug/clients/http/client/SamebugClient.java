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

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.http.entities.bugmate.BugmatesResult;
import com.samebug.clients.http.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.http.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.http.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.http.entities.profile.LoggedInUser;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.CreatedSearch;
import com.samebug.clients.http.entities.search.SearchDetails;
import com.samebug.clients.http.entities.solution.MarkResponse;
import com.samebug.clients.http.entities.solution.RestHit;
import com.samebug.clients.http.entities.solution.Solutions;
import com.samebug.clients.http.entities.solution.Tip;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.exceptions.*;
import com.samebug.clients.http.form.*;
import com.samebug.clients.http.json.Json;
import com.samebug.clients.http.response.GetResponse;
import com.samebug.clients.http.response.PostFormResponse;
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

public final class SamebugClient {
    private final static Logger LOGGER = Logger.getInstance(SamebugClient.class);
    final static Gson gson = Json.gson;
    public static final int TipSourceLoadingTime_Handicap_Millis = 30000;

    final Config config;
    final RestUrlBuilder urlBuilder;
    final RawClient rawClient;

    public SamebugClient(@NotNull final Config config, @Nullable ConnectionService connectionService) {
        this.config = new Config(config);
        this.urlBuilder = new RestUrlBuilder(config.serverRoot);
        this.rawClient = new RawClient(config, connectionService);
    }

    // TODO the server should accept the workspaceId
    public
    @NotNull
    UserInfo getUserInfo(@NotNull final String apiKey) throws SamebugClientException, UnableToPrepareUrl {
        final URL url = urlBuilder.checkApiKey(apiKey);
        HandleGetJson<UserInfo> request = new HandleGetJson<UserInfo>(UserInfo.class) {
            protected HttpGet internalCreateRequest() {
                return new HttpGet(url.toString());
            }
        };
        UserInfo response = extractGet(rawClient.execute(request));
        // NOTE: this is a special case, we handle connection status by the result, not by the http status
        if (rawClient.connectionService != null) rawClient.connectionService.updateAuthenticated(response.getUserExist());
        // TODO tell the connection service if there is a problem with the workspace
        return response;
    }

    public
    @NotNull
    CreatedSearch createSearch(@NotNull final String stacktrace) throws SamebugClientException {
        final URL url = urlBuilder.search();
        HandleAuthenticatedGetJson<CreatedSearch> request = new HandleAuthenticatedGetJson<CreatedSearch>(CreatedSearch.class) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                request.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("exception", stacktrace)), Consts.UTF_8));
                return request;
            }
        };
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    SearchDetails getSearch(@NotNull final Integer searchId) throws SamebugClientException {
        final URL url = urlBuilder.search(searchId);
        HandleSimpleGetJson<SearchDetails> request = new HandleSimpleGetJson<SearchDetails>(url, SearchDetails.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    Solutions getSolutions(@NotNull final Integer searchId) throws SamebugClientException {
        final URL url = urlBuilder.solutions(searchId);
        HandleSimpleGetJson<Solutions> request = new HandleSimpleGetJson<Solutions>(url, Solutions.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    BugmatesResult getBugmates(@NotNull final Integer searchId) throws SamebugClientException {
        final URL url = urlBuilder.bugmates(searchId);
        HandleSimpleGetJson<BugmatesResult> request = new HandleSimpleGetJson<BugmatesResult>(url, BugmatesResult.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    IncomingHelpRequests getIncomingHelpRequests() throws SamebugClientException {
        final URL url = urlBuilder.incomingHelpRequests();
        HandleSimpleGetJson<IncomingHelpRequests> request = new HandleSimpleGetJson<IncomingHelpRequests>(url, IncomingHelpRequests.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    MatchingHelpRequest getHelpRequest(String helpRequestId) throws SamebugClientException {
        final URL url = urlBuilder.getHelpRequest(helpRequestId);
        HandleSimpleGetJson<MatchingHelpRequest> request = new HandleSimpleGetJson<MatchingHelpRequest>(url, MatchingHelpRequest.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    MyHelpRequest createHelpRequest(final int searchId, final String context) throws SamebugClientException, CreateHelpRequest.BadRequest {
        final URL url = urlBuilder.helpRequest();
        HandlePostResponseJson<MyHelpRequest, CreateHelpRequest.Error> request = new HandlePostResponseJson<MyHelpRequest, CreateHelpRequest.Error>(MyHelpRequest.class, CreateHelpRequest.Error.class) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
                form.add(new BasicNameValuePair(CreateHelpRequest.SEARCH_ID, Integer.toString(searchId)));
                form.add(new BasicNameValuePair(CreateHelpRequest.CONTEXT, context));
                request.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
                return request;
            }
        };
        final PostFormResponse<MyHelpRequest, CreateHelpRequest.Error> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new CreateHelpRequest.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    public
    @NotNull
    MyHelpRequest revokeHelpRequest(String helpRequestId) throws SamebugClientException, RevokeHelpRequest.BadRequest {
        final URL url = urlBuilder.revokeHelpRequest(helpRequestId);
        HandleSimplePostJson<MyHelpRequest> request = new HandleSimplePostJson<MyHelpRequest>(url, MyHelpRequest.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    RestHit<Tip> createTip(@NotNull final Integer searchId, @NotNull final String tip, @Nullable final String source, @Nullable final String helpRequestId)
            throws SamebugClientException, CreateTip.BadRequest {
        final URL url = urlBuilder.tip();
        Type typeToken = new TypeToken<RestHit<Tip>>() {
        }.getType();
        HandlePostResponseJson<RestHit<Tip>, CreateTip.Error> request = new HandlePostResponseJson<RestHit<Tip>, CreateTip.Error>(typeToken, CreateTip.Error.class) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                List<BasicNameValuePair> form = new ArrayList<BasicNameValuePair>();
                form.add(new BasicNameValuePair("message", tip));
                form.add(new BasicNameValuePair("searchId", searchId.toString()));
                if (source != null) form.add(new BasicNameValuePair("sourceUrl", source));
                if (helpRequestId != null) form.add(new BasicNameValuePair("helpRequestId", helpRequestId));
                request.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
                // NOTE: posting a tip includes downloading the source on the server side, which might take a while, hence we let it work a bit more.
                request.setConfig(rawClient.requestConfigBuilder.setSocketTimeout(config.requestTimeout + TipSourceLoadingTime_Handicap_Millis).build());
                return request;
            }
        };
        PostFormResponse<RestHit<Tip>, CreateTip.Error> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new CreateTip.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    public
    @NotNull
    MarkResponse postMark(@NotNull final Integer searchId, @NotNull final Integer solutionId) throws SamebugClientException, CreateMark.BadRequest {
        final URL url = urlBuilder.mark();
        HandleAuthenticatedGetJson<MarkResponse> request = new HandleAuthenticatedGetJson<MarkResponse>(MarkResponse.class) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                List<BasicNameValuePair> form = Arrays.asList(new BasicNameValuePair("solution", solutionId.toString()),
                        new BasicNameValuePair("search", searchId.toString()));
                request.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
                return request;
            }
        };
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    MarkResponse retractMark(@NotNull final Integer voteId) throws SamebugClientException, CancelMark.BadRequest {
        final URL url = urlBuilder.cancelMark();
        HandleAuthenticatedGetJson<MarkResponse> request = new HandleAuthenticatedGetJson<MarkResponse>(MarkResponse.class) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                List<BasicNameValuePair> form = Collections.singletonList(new BasicNameValuePair("mark", voteId.toString()));
                request.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));
                return request;
            }
        };
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    UserStats getUserStats() throws SamebugClientException {
        final URL url = urlBuilder.userStats();
        HandleSimpleGetJson<UserStats> request = new HandleSimpleGetJson<UserStats>(url, UserStats.class);
        return extractGet(rawClient.execute(request));
    }

    public
    @NotNull
    LoggedInUser logIn(@NotNull final LogIn.Data data) throws SamebugClientException, LogIn.BadRequest {
        final URL url = urlBuilder.logIn();
        Type responseType = LoggedInUser.class;
        Type badRequestType = new TypeToken<ErrorList<LogIn.ErrorCode>>() {}.getType();
        HandlePostResponseJson<LoggedInUser, ErrorList<LogIn.ErrorCode>> request =
                new HandlePostResponseJson<LoggedInUser, ErrorList<LogIn.ErrorCode>>(responseType, badRequestType) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                request.setEntity(new StringEntity(gson.toJson(data), Consts.UTF_8));
                return request;
            }
        };
        PostFormResponse<LoggedInUser, ErrorList<LogIn.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new LogIn.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    public
    @NotNull
    LoggedInUser signUp(@NotNull final SignUp.Data data) throws SamebugClientException, SignUp.BadRequest {
        final URL url = urlBuilder.signUp();
        Type responseType = LoggedInUser.class;
        Type badRequestType = new TypeToken<ErrorList<SignUp.ErrorCode>>() {}.getType();
        HandlePostResponseJson<LoggedInUser, ErrorList<SignUp.ErrorCode>> request = new HandlePostResponseJson<LoggedInUser, ErrorList<SignUp.ErrorCode>>(responseType, badRequestType) {
            protected HttpPost internalCreateRequest() {
                HttpPost request = new HttpPost(url.toString());
                request.setEntity(new StringEntity(gson.toJson(data), Consts.UTF_8));
                return request;
            }
        };
        PostFormResponse<LoggedInUser, ErrorList<SignUp.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new SignUp.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    public
    @NotNull
    LoggedInUser anonymousUse() throws SamebugClientException {
        final URL url = urlBuilder.anonymousUse();
        HandleSimplePostJson<LoggedInUser> request = new HandleSimplePostJson<LoggedInUser>(url, LoggedInUser.class);
        return extractGet(rawClient.execute(request));
    }

    public HttpRequestBase trace(@NotNull final TrackEvent event) {
        HttpPost post = new HttpPost(config.trackingRoot);
        post.addHeader("Content-Type", "application/json");
        final String json = gson.toJson(event.fields);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }


    <T> T extractGet(GetResponse<T> response) throws SamebugClientException {
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            default:
                throw new IllegalStateException();
        }
    }


    abstract class HandleGetJson<Result> extends HandleRequest<GetResponse<Result>> {
        private Type classOfT;

        HandleGetJson(Type classOfT) {
            this.classOfT = classOfT;
        }

        @Override
        HttpRequestBase createRequest() {
            HttpRequestBase request = internalCreateRequest();
            setJsonResponseType(request);
            return request;
        }

        @Override
        final GetResponse<Result> onSuccess(HttpResponse httpResponse) {
            try {
                Result response = readJsonResponse(httpResponse, classOfT);
                return new GetResponse<Result>(response);
            } catch (JsonParseException e) {
                return new GetResponse<Result>(new JsonParseException("Failed to parse json response", e));
            } catch (HttpError httpError) {
                return new GetResponse<Result>(httpError);
            }
        }

        @Override
        final GetResponse<Result> onBadRequest(HttpResponse response) {
            return new GetResponse<Result>(new BadRequest());
        }

        @Override
        final GetResponse<Result> onError(SamebugClientException exception) {
            return new GetResponse<Result>(exception);
        }

        protected abstract HttpRequestBase internalCreateRequest();
    }

    abstract class HandleAuthenticatedGetJson<Result> extends HandleGetJson<Result> {
        HandleAuthenticatedGetJson(Type classOfT) {
            super(classOfT);
        }

        @Override
        final HttpRequestBase createRequest() {
            HttpRequestBase request = super.createRequest();
            addAuthentication(request);
            return request;
        }
    }

    final class HandleSimpleGetJson<Result> extends HandleAuthenticatedGetJson<Result> {
        private final URL url;

        HandleSimpleGetJson(URL url, Type classOfT) {
            super(classOfT);
            this.url = url;
        }

        @Override
        protected HttpGet internalCreateRequest() {
            return new HttpGet(url.toString());
        }
    }

    final class HandleSimplePostJson<Result> extends HandleAuthenticatedGetJson<Result> {
        private final URL url;

        HandleSimplePostJson(URL url, Type classOfT) {
            super(classOfT);
            this.url = url;
        }

        @Override
        protected HttpPost internalCreateRequest() {
            return new HttpPost(url.toString());
        }
    }

    abstract class HandlePostResponseJson<Result, FormError> extends HandleRequest<PostFormResponse<Result, FormError>> {
        private final Type resultType;
        private final Type formErrorType;

        HandlePostResponseJson(Type resultType, Type formErrorType) {
            this.resultType = resultType;
            this.formErrorType = formErrorType;
        }

        @Override
        HttpPost createRequest() {
            HttpPost request = internalCreateRequest();
            setJsonContentType(request);
            setJsonResponseType(request);
            return request;
        }

        @Override
        PostFormResponse<Result, FormError> onSuccess(HttpResponse httpResponse) {
            try {
                Result response = readJsonResponse(httpResponse, resultType);
                return PostFormResponse.fromResult(response);
            } catch (JsonParseException e) {
                SamebugClientException exception = new JsonParseException("Failed to parse json response", e);
                return PostFormResponse.fromException(exception);
            } catch (HttpError httpError) {
                return PostFormResponse.fromException(httpError);
            }
        }

        @Override
        PostFormResponse<Result, FormError> onBadRequest(HttpResponse httpResponse) {
            try {
                FormError response = readJsonResponse(httpResponse, formErrorType);
                return PostFormResponse.fromFormError(response);
            } catch (JsonParseException e) {
                SamebugClientException exception = new JsonParseException("Failed to parse json response", e);
                return PostFormResponse.fromException(exception);
            } catch (HttpError httpError) {
                return PostFormResponse.fromException(httpError);
            }
        }

        @Override
        PostFormResponse<Result, FormError> onError(SamebugClientException exception) {
            return PostFormResponse.fromException(exception);
        }

        protected abstract HttpPost internalCreateRequest();
    }

    void setJsonContentType(HttpRequestBase request) {
        request.setHeader("Content-Type", "application/json");
    }

    void setJsonResponseType(HttpRequestBase request) {
        request.setHeader("Accept", "application/json");
    }

    void addAuthentication(HttpRequestBase request) {
        request.addHeader("X-Samebug-ApiKey", config.apiKey);
        if (config.workspaceId != null) request.addHeader("X-Samebug-WorkspaceId", config.workspaceId.toString());
    }

    <T> T readJsonResponse(HttpResponse response, Type classOfT) throws HttpError, JsonParseException {
        InputStream content = null;
        Reader reader = null;
        String json = null;
        try {
            content = response.getEntity().getContent();
            reader = new InputStreamReader(content, "UTF-8");
            if (config.isJsonDebugEnabled) {
                json = CharStreams.toString(reader);
                return gson.fromJson(json, classOfT);
            } else {
                return gson.fromJson(reader, classOfT);
            }
        } catch (com.google.gson.JsonParseException e) {
            throw new JsonParseException(json, e);
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
