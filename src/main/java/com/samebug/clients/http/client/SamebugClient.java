/*
 * Copyright 2018 Samebug, Inc.
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
import com.samebug.clients.http.entities.jsonapi.*;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.NewSearch;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.UserUnauthenticated;
import com.samebug.clients.http.form.LogIn;
import com.samebug.clients.http.form.SignUp;
import com.samebug.clients.http.json.Json;
import com.samebug.clients.http.response.GetResponse;
import com.samebug.clients.http.response.PostFormResponse;
import org.apache.http.client.methods.HttpPost;
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
                .get()
                .withResponseType(MeResource.class);
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public UserStats getUserStats() throws SamebugClientException {
        if (config.userId == null) throw new UserUnauthenticated();
        Builder.SimpleResponseHandler<UserStatsResource> request = requestBuilder
                .at(uriBuilder.userStats(config.userId))
                .get()
                .withResponseType(UserStatsResource.class);
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public CreatedSearchResource createSearch(@NotNull final NewSearch data) throws SamebugClientException {
        Builder.SimpleResponseHandler<CreatedSearchResource> request = requestBuilder
                .at(uriBuilder.searches())
                .post(data)
                .withResponseType(CreatedSearchResource.class);
        return extractResponse(rawClient.execute(request));
    }

    @NotNull
    public Search getSearch(@NotNull final Integer searchId) throws SamebugClientException {
        Builder.SimpleResponseHandler<SearchResource> request = requestBuilder
                .at(uriBuilder.searches(searchId))
                .get()
                .withResponseType(SearchResource.class);
        return extractResponse(rawClient.execute(request)).getData();
    }

    @NotNull
    public AuthenticationResponse logIn(@NotNull final LogIn.Data data) throws SamebugClientException, LogIn.BadRequest {
        Builder.BadRequestCapableResponseJson<AuthenticationResponseResource, JsonErrors<LogIn.ErrorCode>> request = requestBuilder
                .at(uriBuilder.logIn())
                .post(data)
                .unauthenticated()
                .<JsonErrors<LogIn.ErrorCode>>withFormErrorType(new TypeToken<JsonErrors<LogIn.ErrorCode>>() {}.getType())
                .withResponseType(AuthenticationResponseResource.class);
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
        Builder.BadRequestCapableResponseJson<AuthenticationResponseResource, JsonErrors<SignUp.ErrorCode>> request = requestBuilder
                .at(uriBuilder.signUp())
                .post(data)
                .unauthenticated()
                .<JsonErrors<SignUp.ErrorCode>>withFormErrorType(new TypeToken<JsonErrors<SignUp.ErrorCode>>() {}.getType())
                .withResponseType(AuthenticationResponseResource.class);
        // NOTE: this is a bit leaky (mutating through a field), but otherwise it would be so overcomplicated...
        request.getRequest().setHeader("Cookie", "registration-hook=unauthenticated");
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
                .post()
                .unauthenticated()
                .withResponseType(AuthenticationResponseResource.class);
        // NOTE: this is a bit leaky (mutating through a field), but otherwise it would be so overcomplicated...
        request.getRequest().setHeader("Cookie", "registration-hook=unauthenticated");
        return extractResponse(rawClient.execute(request)).getData();
    }

    public void trace(@NotNull final TrackEvent event) throws SamebugClientException {
        HttpPost post = new HttpPost(config.trackingRoot);
        post.addHeader("Content-Type", "application/json");
        final String json = gson.toJson(event.fields);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        rawClient.executeTracking(post);
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
