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

import org.jetbrains.annotations.NotNull;

import java.net.URI;

final class RestUriBuilder {
    @NotNull
    private final URI gateway;

    RestUriBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        if ("http://localhost:9000".equals(serverRoot) || "http://127.0.0.1:9000".equals(serverRoot)) this.gateway = URI.create(serverRoot + "/");
        else this.gateway = URI.create(serverRoot + "/").resolve("rest/1.0");
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
    public URI me() {
        return resolve("auth/me");
    }

    @NotNull
    public URI userStats(@NotNull final Integer userId) {
        return resolve("users/" + userId + "/stats");
    }

    @NotNull
    public URI anonymousSignUp() {
        return resolve("auth/signup-anonymously");
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
