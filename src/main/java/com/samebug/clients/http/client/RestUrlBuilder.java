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

import com.samebug.clients.http.exceptions.IllegalUriException;
import com.samebug.clients.http.exceptions.UnableToPrepareUrl;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

final class RestUrlBuilder {
    @NotNull
    final URI gateway;

    public RestUrlBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        if (serverRoot.equals("http://localhost:9000")) this.gateway = URI.create(serverRoot + "/");
        else this.gateway = URI.create(serverRoot + "/").resolve("rest/");
    }

    @NotNull
    public URL search() {
        return resolve("search");
    }

    @NotNull
    public URL search(@NotNull final Integer searchId) {
        return resolve("search/" + searchId);
    }

    @NotNull
    public URL solutions(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/external-solutions");
    }

    @NotNull
    public URL tips(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/tips");
    }

    @NotNull
    public URL bugmates(@NotNull final Integer searchId) {
        return resolve("search/" + searchId + "/bugmates");
    }

    @NotNull
    public URL checkApiKey(@NotNull final String apiKey) throws UnableToPrepareUrl {
        try {
            final String uri = "checkApiKey?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
            return resolve(uri);
        } catch (UnsupportedEncodingException e) {
            throw new UnableToPrepareUrl("Unable to resolve uri with apiKey " + apiKey, e);
        }
    }

    @NotNull
    public URL helpRequest() {
        return resolve("help-request");
    }

    @NotNull
    public URL revokeHelpRequest(String id) {
        return resolve("help-request/" + id + "/revoke");
    }

    @NotNull
    public URL getHelpRequest(String helpRequestId) {
        return resolve("help-request/" + helpRequestId);
    }

    @NotNull
    public URL incomingHelpRequests() {
        return resolve("incoming-helprequests");
    }

    @NotNull
    public URL tip() {
        return resolve("tip");
    }

    @NotNull
    public URL mark() {
        return resolve("mark");
    }

    @NotNull
    public URL cancelMark(@NotNull final Integer markId) {
        return resolve("mark/" + markId + "cancel");
    }

    @NotNull
    public URL userStats() {
        return resolve("user/statistics");
    }

    // TODO
    @NotNull
    public URL anonymousUse() {
        return resolve("signup-anonymously");
    }

    @NotNull
    public URL signUp() {
        return resolve("auth/signup");
    }

    @NotNull
    public URL logIn() {
        return resolve("auth/signin");
    }


    @NotNull
    URL resolve(@NotNull final String uri) throws IllegalUriException {
        try {
            return gateway.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }
}
