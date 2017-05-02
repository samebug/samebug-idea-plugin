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
package com.samebug.clients.common.services;

import com.samebug.clients.http.exceptions.IllegalUriException;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public final class WebUrlBuilder {
    @NotNull
    final URI serverRoot;

    public WebUrlBuilder(@NotNull final String serverRoot) {
        assert !serverRoot.endsWith("/");
        this.serverRoot = URI.create(serverRoot);
    }

    @NotNull
    public URI getServerRoot() {
        return serverRoot;
    }

    @NotNull
    public URL search(@NotNull final Integer searchId) {
        return resolveToRoot("/search/" + searchId);
    }

    @NotNull
    public URL assets(@NotNull final String assetUri) {
        return resolveToRoot("/assets/" + assetUri);
    }

    @NotNull
    public URL sourceIcon(@NotNull final String iconId) {
        return resolveToRoot("/assets/images/sources/" + iconId + ".png");
    }

    @NotNull
    public URL profile(@NotNull final int userId) {
        // TODO profile page url
        return resolveToRoot("/user/" + userId);
    }

    @NotNull
    public URL forgottenPassword() {
        // TODO forgotten password page
        return resolveToRoot("");
    }

    @NotNull
    URL resolveToRoot(@NotNull final String uri) throws IllegalUriException {
        try {
            return serverRoot.resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    String enc(final String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "utf-8");
    }
}
