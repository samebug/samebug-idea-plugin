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
package com.samebug.clients.common.api;

import com.samebug.clients.common.api.exceptions.IllegalUriException;
import com.samebug.clients.common.api.exceptions.UnableToPrepareUrl;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

final public class RestUrlBuilder {
    final static String API_VERSION = "0.11";
    @NotNull
    final URI gateway;

    public RestUrlBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        this.gateway = URI.create(serverRoot + "/").resolve("rest/").resolve(API_VERSION + "/");
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
    public URL history() {
        return resolve("history");
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
    public URL cancelMark() {
        return resolve("mark/cancel");
    }

    @NotNull
    public URL userStats() {
        return resolve("user/statistics");
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
