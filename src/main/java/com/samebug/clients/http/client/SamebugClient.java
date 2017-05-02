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
import com.samebug.clients.http.entities.helprequest.IncomingHelpRequestList;
import com.samebug.clients.http.entities.jsonapi.JsonErrors;
import com.samebug.clients.http.entities.mark.MarkCancelled;
import com.samebug.clients.http.entities.mark.MarkCreated;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.response.*;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.exceptions.SamebugClientException;
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

import java.net.URI;

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
                apiKey, workspaceId, config.serverRoot, config.trackingRoot, config.isTrackingEnabled,
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
        final URI url = uriBuilder.userStats();
        // TODO
        return new UserStats();
    }

    @NotNull
    public SearchRequest createSearch(@NotNull final SearchCreate data) throws SamebugClientException {
        Builder.SimplePostHandler<SearchRequest, SearchCreate> request = requestBuilder
                .at(uriBuilder.search())
                .<SearchRequest>withResponse(SearchRequest.class)
                .posting(data)
                .build();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public SearchRequest getSearch(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<SearchRequest> request = requestBuilder
                .at(uriBuilder.search(searchId))
                .<SearchRequest>withResponse(SearchRequest.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetSolutions getSolutions(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetSolutions> request = requestBuilder
                .at(uriBuilder.solutions(searchId))
                .<GetSolutions>withResponse(GetSolutions.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetTips getTips(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetTips> request = requestBuilder
                .at(uriBuilder.tips(searchId))
                .<GetTips>withResponse(GetTips.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public GetBugmates getBugmates(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetBugmates> request = requestBuilder
                .at(uriBuilder.bugmates(searchId))
                .<GetBugmates>withResponse(GetBugmates.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public IncomingHelpRequestList getIncomingHelpRequests() throws SamebugClientException {
        Builder.SimpleResponseHandler<IncomingHelpRequestList> request = requestBuilder
                .at(uriBuilder.incomingHelpRequests())
                .<IncomingHelpRequestList>withResponse(IncomingHelpRequestList.class)
                .buildGet();
        return extractGet(rawClient.execute(request));
    }

    @NotNull
    public HelpRequest getHelpRequest(String helpRequestId) throws SamebugClientException {
        Builder.SimpleResponseHandler<GetHelpRequest> request = requestBuilder
                .at(uriBuilder.getHelpRequest(helpRequestId))
                .<GetHelpRequest>withResponse(GetHelpRequest.class)
                .buildGet();
        return extractGet(rawClient.execute(request)).getData();
    }

    @NotNull
    public HelpRequest createHelpRequest(@NotNull final HelpRequestCreate.Data data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        Builder.HandlePostResponseJson<HelpRequestCreate.Data, CreateHelpRequest, JsonErrors<HelpRequestCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.helpRequest())
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
    public HelpRequest revokeHelpRequest(String helpRequestId) throws SamebugClientException, HelpRequestCancel.BadRequest {
        Builder.HandlePostResponseJson<?, CreateHelpRequest, JsonErrors<HelpRequestCancel.ErrorCode>> request = requestBuilder
                .at(uriBuilder.revokeHelpRequest(helpRequestId))
                .<CreateHelpRequest>withResponse(CreateHelpRequest.class)
                .posting(null)
                .<JsonErrors<HelpRequestCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<HelpRequestCancel.ErrorCode>>() {}.getType())
                .buildPut();
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
    public SolutionSlot<SamebugTip> createTip(@NotNull final TipCreate.Base data) throws SamebugClientException, TipCreate.BadRequest {
        // NOTE: posting a tip includes downloading the source on the server side, which might take a while, so maybe we should allow longer timeout
        Builder.HandlePostResponseJson<TipCreate.Base, CreateTipResponse, JsonErrors<TipCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.tip())
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
    public MarkCreated postMark(@NotNull final MarkCreate.Data data) throws SamebugClientException, MarkCreate.BadRequest {
        Builder.HandlePostResponseJson<MarkCreate.Data, MarkCreated, JsonErrors<MarkCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.mark())
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
                .at(uriBuilder.cancelMark(markId))
                .<MarkCancelled>withResponse(MarkCancelled.class)
                .posting(null)
                .<JsonErrors<MarkCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<MarkCancel.ErrorCode>>() {}.getType())
                .buildPost();
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
