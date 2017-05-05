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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.http.entities.jsonapi.JsonErrors;
import com.samebug.clients.http.entities.jsonapi.JsonResource;
import com.samebug.clients.http.entities.mark.MarkCancelled;
import com.samebug.clients.http.entities.mark.MarkCreated;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.response.*;
import com.samebug.clients.http.entities.search.NewSearch;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.UserUnauthenticated;
import com.samebug.clients.http.form.*;
import com.samebug.clients.http.json.Json;
import com.samebug.clients.http.response.GetResponse;
import com.samebug.clients.http.response.PostFormResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public final class SamebugClient {
    private static final Gson gson = Json.gson;

    private final Config config;
    private final RestUriBuilder uriBuilder;
    private final RawClient rawClient;
    private final Builder requestBuilder;

    public SamebugClient(@NotNull final Config config, @Nullable ConnectionService connectionService) {
        this.config = new Config(config);
        this.uriBuilder = new RestUriBuilder(config.serverRoot);
        this.rawClient = new RawClient(config, connectionService);
        this.requestBuilder = new Builder(config);
    }

    @NotNull
    public UserInfo getUserInfo(@NotNull final String apiKey, @Nullable final Integer workspaceId) throws SamebugClientException {
        RawClient tmpClient = new RawClient(new Config(
                apiKey, null, workspaceId, config.serverRoot, config.trackingRoot, config.isTrackingEnabled,
                config.connectTimeout, config.requestTimeout, config.isApacheLoggingEnabled, config.isJsonDebugEnabled, config.proxy),
                null
        );
        Builder.SimpleResponseHandler<GetUser> request = requestBuilder
                .at(uriBuilder.me())
                .<GetUser>withResponse(GetUser.class)
                .buildGet();
        return extractGet(tmpClient.execute(request)).getData();
    }

    @NotNull
    public UserStats getUserStats() throws SamebugClientException {
        if (config.userId == null) throw new UserUnauthenticated();
        Type responseType = new TypeToken<JsonResource<UserStats>>() {}.getType();
        Builder.SimpleResponseHandler<JsonResource<UserStats>> request = requestBuilder
                .at(uriBuilder.userStats(config.userId))
                .<JsonResource<UserStats>>withResponse(responseType)
                .buildGet();
        return extractGet(rawClient.execute(request)).getData();
    }

    @NotNull
    public CreatedSearch createSearch(@NotNull final NewSearch data) throws SamebugClientException {
        Builder.SimplePostHandler<CreatedSearch, NewSearch> request = requestBuilder
                .at(uriBuilder.searches())
                .<CreatedSearch>withResponse(CreatedSearch.class)
                .posting(data)
                .build();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public CreatedSearch getSearch(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<CreatedSearch> request = requestBuilder
                .at(uriBuilder.searches(searchId))
                .<CreatedSearch>withResponse(CreatedSearch.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetSolutions getSolutions(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetSolutions> request = requestBuilder
                .at(uriBuilder.solutionsForSearch(searchId))
                .<GetSolutions>withResponse(GetSolutions.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetTips getTips(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetTips> request = requestBuilder
                .at(uriBuilder.tipsForSearch(searchId))
                .<GetTips>withResponse(GetTips.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetBugmates getBugmates(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetBugmates> request = requestBuilder
                .at(uriBuilder.bugmatesForSearch(searchId))
                .<GetBugmates>withResponse(GetBugmates.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public IncomingHelpRequestList getIncomingHelpRequests() throws SamebugClientException {
        if (config.userId == null) throw new UserUnauthenticated();
        Builder.SimpleResponseHandler<IncomingHelpRequestList> request = requestBuilder
                .at(uriBuilder.incomingHelpRequests(config.userId))
                .<IncomingHelpRequestList>withResponse(IncomingHelpRequestList.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public HelpRequest getHelpRequest(String helpRequestId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetHelpRequest> request = requestBuilder
                .at(uriBuilder.helpRequest(helpRequestId))
                .<GetHelpRequest>withResponse(GetHelpRequest.class)
                .buildGet();
        return extractGet(rawClient.execute(request)).getData();
    }

    @NotNull
    public HelpRequest createHelpRequest(@NotNull final Integer searchId, @NotNull final NewHelpRequest data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        Builder.HandlePostResponseJson<NewHelpRequest, CreateHelpRequest, JsonErrors<HelpRequestCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.helpRequests(searchId))
                .<CreateHelpRequest>withResponse(CreateHelpRequest.class)
                .posting(data)
                .<JsonErrors<HelpRequestCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<HelpRequestCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<CreateHelpRequest, JsonErrors<HelpRequestCreate.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new HelpRequestCreate.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public HelpRequest cancelHelpRequest(@NotNull final String helpRequestId) throws SamebugClientException, HelpRequestCancel.BadRequest {
        Builder.HandlePostResponseJson<?, CreateHelpRequest, JsonErrors<HelpRequestCancel.ErrorCode>> request = requestBuilder
                .at(uriBuilder.helpRequest(helpRequestId))
                .<CreateHelpRequest>withResponse(CreateHelpRequest.class)
                .posting(null)
                .<JsonErrors<HelpRequestCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<HelpRequestCancel.ErrorCode>>() {}.getType())
                .buildDelete();
        final PostFormResponse<CreateHelpRequest, JsonErrors<HelpRequestCancel.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new HelpRequestCancel.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public SearchHit<SamebugTip> createTip(@NotNull final Integer searchId, @NotNull final NewSearchHit data) throws SamebugClientException, TipCreate.BadRequest {
        // NOTE: posting a tip includes downloading the source on the server side, which might take a while, so maybe we should allow longer timeout
        Builder.HandlePostResponseJson<NewSearchHit, CreateTipResponse, JsonErrors<TipCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.tipsForSearch(searchId))
                .<CreateTipResponse>withResponse(CreateTipResponse.class)
                .posting(data)
                .<JsonErrors<TipCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<TipCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<CreateTipResponse, JsonErrors<TipCreate.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new TipCreate.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public MarkCreated postMark(@NotNull final Integer searchId, @NotNull final NewMark data) throws SamebugClientException, MarkCreate.BadRequest {
        Builder.HandlePostResponseJson<NewMark, MarkCreated, JsonErrors<MarkCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.marksForSearch(searchId))
                .<MarkCreated>withResponse(MarkCreated.class)
                .posting(data)
                .<JsonErrors<MarkCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<MarkCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<MarkCreated, JsonErrors<MarkCreate.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new MarkCreate.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public MarkCancelled cancelMark(@NotNull final Integer markId) throws SamebugClientException, MarkCancel.BadRequest {
        Builder.HandlePostResponseJson<?, MarkCancelled, JsonErrors<MarkCancel.ErrorCode>> request = requestBuilder
                .at(uriBuilder.mark(markId))
                .<MarkCancelled>withResponse(MarkCancelled.class)
                .posting(null)
                .<JsonErrors<MarkCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<MarkCancel.ErrorCode>>() {}.getType())
                .buildDelete();
        final PostFormResponse<MarkCancelled, JsonErrors<MarkCancel.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new MarkCancel.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public AuthenticationResponse logIn(@NotNull final LogIn.Data data) throws SamebugClientException, LogIn.BadRequest {
        Builder.HandlePostResponseJson<LogIn.Data, AuthenticateRequest, JsonErrors<LogIn.ErrorCode>> request = requestBuilder
                .at(uriBuilder.logIn())
                .unauthenticated()
                .<AuthenticateRequest>withResponse(AuthenticateRequest.class)
                .posting(data)
                .<JsonErrors<LogIn.ErrorCode>>withErrors(new TypeToken<JsonErrors<LogIn.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<AuthenticateRequest, JsonErrors<LogIn.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new LogIn.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public AuthenticationResponse signUp(@NotNull final SignUp.Data data) throws SamebugClientException, SignUp.BadRequest {
        Builder.HandlePostResponseJson<SignUp.Data, AuthenticateRequest, JsonErrors<SignUp.ErrorCode>> request = requestBuilder
                .at(uriBuilder.signUp())
                .unauthenticated()
                .<AuthenticateRequest>withResponse(AuthenticateRequest.class)
                .posting(data)
                .<JsonErrors<SignUp.ErrorCode>>withErrors(new TypeToken<JsonErrors<SignUp.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<AuthenticateRequest, JsonErrors<SignUp.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new SignUp.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public AuthenticationResponse anonymousUse() throws SamebugClientException {
        Builder.SimpleResponseHandler<AuthenticateRequest> request = requestBuilder
                .at(uriBuilder.anonymousUse())
                .unauthenticated()
                .<AuthenticateRequest>withResponse(AuthenticateRequest.class)
                .buildPost();
        return extractGet(rawClient.execute(request)).getData();
    }

    public HttpRequestBase trace(@NotNull final TrackEvent event) {
        HttpPost post = new HttpPost(config.trackingRoot);
        post.addHeader("Content-Type", "application/json");
        final String json = gson.toJson(event.fields);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }


    private <T> T extractGet(GetResponse<T> response) throws SamebugClientException {
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult();
            case EXCEPTION:
                throw response.getException();
            default:
                throw new IllegalStateException();
        }
    }
}
