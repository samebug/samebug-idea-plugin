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
    private final URI gateway;

    RestUriBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        if ("http://localhost:9000".equals(serverRoot) || "http://127.0.0.1:9000".equals(serverRoot)) this.gateway = URI.create(serverRoot + "/");
        else this.gateway = URI.create(serverRoot + "/").resolve("rest/");
    }

    @NotNull
    public URI searches() {
        return resolve("searches");
    }

    @NotNull
    public URI searches(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId);
    }

    @NotNull
    public URI solutionsForSearch(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId + "/external-solutions");
    }

    @NotNull
    public URI tipsForSearch(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId + "/tips");
    }

    @NotNull
    public URI bugmatesForSearch(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId + "/bugmates");
    }

    @NotNull
    public URI me() {
        return resolve("auth/me");
    }

    @NotNull
    public URI helpRequests(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId + "/help-requests");
    }

    @NotNull
    public URI helpRequest(String helpRequestId) {
        return resolve("help-requests/" + helpRequestId);
    }

    @NotNull
    public URI marksForSearch(@NotNull final Integer searchId) {
        return resolve("searches/" + searchId + "/marks");
    }

    @NotNull
    public URI mark(@NotNull final Integer markId) {
        return resolve("marks/" + markId);
    }

    @NotNull
    public URI incomingHelpRequests(@NotNull final Integer userId) {
        return resolve("users/" + userId + "/help-requests");
    }

    @NotNull
    public URI userStats(@NotNull final Integer userId) {
        return resolve("users/" + userId + "stats");
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
    private URI resolve(@NotNull final String uri) {
        return gateway.resolve(uri);
    }
}
