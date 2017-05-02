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

import org.jetbrains.annotations.NotNull;

import java.net.URI;

final class RestUriBuilder {
    @NotNull
    final URI gateway;

    RestUriBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        if ("http://localhost:9000".equals(serverRoot) || "http://127.0.0.1:9000".equals(serverRoot)) this.gateway = URI.create(serverRoot + "/");
        else this.gateway = URI.create(serverRoot + "/").resolve("rest/");
    }

    @NotNull
    public URI search() {
        return resolve("search");
    }

    @NotNull
    public URI search(@NotNull final Integer searchId) {
        return resolve("search/" + searchId);
    }

    @NotNull
    public URI solutions(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/external-solutions");
    }

    @NotNull
    public URI tips(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/tips");
    }

    @NotNull
    public URI bugmates(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/bugmates");
    }

    @NotNull
    public URI me() {
        return resolve("auth/me");
    }

    @NotNull
    public URI helpRequest() {
        return resolve("help-request");
    }

    @NotNull
    public URI revokeHelpRequest(String id) {
        return resolve("help-request/" + id + "/revoke");
    }

    @NotNull
    public URI getHelpRequest(String helpRequestId) {
        return resolve("help-request/" + helpRequestId);
    }

    @NotNull
    public URI incomingHelpRequests() {
        return resolve("incoming-helprequests");
    }

    @NotNull
    public URI tip() {
        return resolve("tip");
    }

    @NotNull
    public URI mark() {
        return resolve("mark");
    }

    @NotNull
    public URI cancelMark(@NotNull final Integer markId) {
        return resolve("mark/" + markId + "cancel");
    }

    @NotNull
    public URI userStats() {
        return resolve("user/statistics");
    }

    // TODO
    @NotNull
    public URI anonymousUse() {
        return resolve("signup-anonymously");
    }

    @NotNull
    public URI signUp() {
        return resolve("auth/signup");
    }

    @NotNull
    public URI logIn() {
        return resolve("auth/signin");
    }


    @NotNull
    URI resolve(@NotNull final String uri) {
        return gateway.resolve(uri);
    }
}
