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
import com.samebug.clients.http.entities.jsonapi.*;
import com.samebug.clients.http.entities.mark.Mark;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.NewSearch;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.entities.user.Me;
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

public final class SamebugClient {
    @NotNull
    private static final Gson gson = Json.gson;

    @NotNull
    private final Config config;
    @NotNull
    private final RestUriBuilder uriBuilder;
    @NotNull
    private final RawClient rawClient;
    @NotNull
    private final Builder requestBuilder;

    public SamebugClient(@NotNull final Config config, @Nullable final ConnectionService connectionService) {
        this.config = new Config(config);
        this.uriBuilder = new RestUriBuilder(config.serverRoot);
        this.rawClient = new RawClient(config, connectionService);
        this.requestBuilder = new Builder(config);
    }

    @NotNull
    public Me getUserInfo() throws SamebugClientException {
        Builder.SimpleResponseHandler<MeResource> request = requestBuilder
                .at(uriBuilder.me())
                .<MeResource>withResponse(MeResource.class)
                .buildGet();
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public UserStats getUserStats() throws SamebugClientException {
        if (config.userId == null) throw new UserUnauthenticated();
        Builder.SimpleResponseHandler<UserStatsResource> request = requestBuilder
                .at(uriBuilder.userStats(config.userId))
                .<UserStatsResource>withResponse(UserStatsResource.class)
                .buildGet();
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public CreatedSearchResource createSearch(@NotNull final NewSearch data) throws SamebugClientException {
        Builder.SimplePostHandler<CreatedSearchResource, NewSearch> request = requestBuilder
                .at(uriBuilder.searches())
                .<CreatedSearchResource>withResponse(CreatedSearchResource.class)
                .posting(data)
                .build();
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public Search getSearch(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<SearchResource> request = requestBuilder
                .at(uriBuilder.searches(searchId))
                .<SearchResource>withResponse(SearchResource.class)
                .buildGet();
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public SolutionList getSolutions(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<SolutionList> request = requestBuilder
                .at(uriBuilder.solutionsForSearch(searchId))
                .<SolutionList>withResponse(SolutionList.class)
                .buildGet();
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public TipList getTips(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<TipList> request = requestBuilder
                .at(uriBuilder.tipsForSearch(searchId))
                .<TipList>withResponse(TipList.class)
                .buildGet();
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public BugmateList getBugmates(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<BugmateList> request = requestBuilder
                .at(uriBuilder.bugmatesForSearch(searchId))
                .<BugmateList>withResponse(BugmateList.class)
                .buildGet();
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public IncomingHelpRequestList getIncomingHelpRequests() throws SamebugClientException {
        if (config.userId == null) throw new UserUnauthenticated();
        Builder.SimpleResponseHandler<IncomingHelpRequestList> request = requestBuilder
                .at(uriBuilder.incomingHelpRequests(config.userId))
                .<IncomingHelpRequestList>withResponse(IncomingHelpRequestList.class)
                .buildGet();
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public HelpRequest getHelpRequest(@NotNull final String helpRequestId) throws SamebugClientException {
        Builder.SimpleResponseHandler<HelpRequestResource> request = requestBuilder
                .at(uriBuilder.helpRequest(helpRequestId))
                .<HelpRequestResource>withResponse(HelpRequestResource.class)
                .buildGet();
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public HelpRequest createHelpRequest(@NotNull final Integer searchId, @NotNull final NewHelpRequest data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        Builder.HandlePostResponseJson<NewHelpRequest, HelpRequestResource, JsonErrors<HelpRequestCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.helpRequests(searchId))
                .<HelpRequestResource>withResponse(HelpRequestResource.class)
                .posting(data)
                .<JsonErrors<HelpRequestCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<HelpRequestCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<HelpRequestResource, JsonErrors<HelpRequestCreate.ErrorCode>> response = rawClient.execute(request);
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
        Builder.HandlePostResponseJson<?, HelpRequestResource, JsonErrors<HelpRequestCancel.ErrorCode>> request = requestBuilder
                .at(uriBuilder.helpRequest(helpRequestId))
                .<HelpRequestResource>withResponse(HelpRequestResource.class)
                .posting(null)
                .<JsonErrors<HelpRequestCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<HelpRequestCancel.ErrorCode>>() {}.getType())
                .buildDelete();
        final PostFormResponse<HelpRequestResource, JsonErrors<HelpRequestCancel.ErrorCode>> response = rawClient.execute(request);
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
        Builder.HandlePostResponseJson<NewSearchHit, CreateTipResource, JsonErrors<TipCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.tipsForSearch(searchId))
                .<CreateTipResource>withResponse(CreateTipResource.class)
                .posting(data)
                .<JsonErrors<TipCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<TipCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<CreateTipResource, JsonErrors<TipCreate.ErrorCode>> response = rawClient.execute(request);
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
    public Mark postMark(@NotNull final Integer searchId, @NotNull final NewMark data) throws SamebugClientException, MarkCreate.BadRequest {
        Builder.HandlePostResponseJson<NewMark, MarkResource, JsonErrors<MarkCreate.ErrorCode>> request = requestBuilder
                .at(uriBuilder.marksForSearch(searchId))
                .<MarkResource>withResponse(MarkResource.class)
                .posting(data)
                .<JsonErrors<MarkCreate.ErrorCode>>withErrors(new TypeToken<JsonErrors<MarkCreate.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<MarkResource, JsonErrors<MarkCreate.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
            case EXCEPTION:
                throw response.getException();
            case FORM_ERROR:
                throw new MarkCreate.BadRequest(response.getFormError());
            default:
                throw new IllegalStateException();
        }
    }

    @NotNull
    public Mark cancelMark(@NotNull final Integer markId) throws SamebugClientException, MarkCancel.BadRequest {
        Builder.HandlePostResponseJson<?, MarkResource, JsonErrors<MarkCancel.ErrorCode>> request = requestBuilder
                .at(uriBuilder.mark(markId))
                .<MarkResource>withResponse(MarkResource.class)
                .posting(null)
                .<JsonErrors<MarkCancel.ErrorCode>>withErrors(new TypeToken<JsonErrors<MarkCancel.ErrorCode>>() {}.getType())
                .buildDelete();
        final PostFormResponse<MarkResource, JsonErrors<MarkCancel.ErrorCode>> response = rawClient.execute(request);
        switch (response.getResultType()) {
            case SUCCESS:
                return response.getResult().getData();
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
        Builder.HandlePostResponseJson<LogIn.Data, AuthenticationResponseResource, JsonErrors<LogIn.ErrorCode>> request = requestBuilder
                .at(uriBuilder.logIn())
                .unauthenticated()
                .<AuthenticationResponseResource>withResponse(AuthenticationResponseResource.class)
                .posting(data)
                .<JsonErrors<LogIn.ErrorCode>>withErrors(new TypeToken<JsonErrors<LogIn.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<AuthenticationResponseResource, JsonErrors<LogIn.ErrorCode>> response = rawClient.execute(request);
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
        Builder.HandlePostResponseJson<SignUp.Data, AuthenticationResponseResource, JsonErrors<SignUp.ErrorCode>> request = requestBuilder
                .at(uriBuilder.signUp())
                .unauthenticated()
                .<AuthenticationResponseResource>withResponse(AuthenticationResponseResource.class)
                .posting(data)
                .<JsonErrors<SignUp.ErrorCode>>withErrors(new TypeToken<JsonErrors<SignUp.ErrorCode>>() {}.getType())
                .buildPost();
        final PostFormResponse<AuthenticationResponseResource, JsonErrors<SignUp.ErrorCode>> response = rawClient.execute(request);
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
        Builder.SimpleResponseHandler<AuthenticationResponseResource> request = requestBuilder
                .at(uriBuilder.anonymousSignUp())
                .unauthenticated()
                .<AuthenticationResponseResource>withResponse(AuthenticationResponseResource.class)
                .buildPost();
        return extractResponse(rawClient.execute(request)).getData();
    }

    public HttpRequestBase trace(@NotNull final TrackEvent event) {
        HttpPost post = new HttpPost(config.trackingRoot);
        post.addHeader("Content-Type", "application/json");
        final String json = gson.toJson(event.fields);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }


    private <T> T extractResponse(GetResponse<T> response) throws SamebugClientException {
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
