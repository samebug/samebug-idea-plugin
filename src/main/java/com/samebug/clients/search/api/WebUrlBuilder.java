/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.search.api;

import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.exceptions.IllegalUriException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

final public class WebUrlBuilder {
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

    @Nullable
    public URL crashdoc(@NotNull final BreadCrumb b) {
        // TODO currently we can decide between library- and application components
        // only by the component color (0 for application components)
        if (b.component.color == 0) return null;
        else {
            try {
                // TODO handle default package?
                final String entryUri = enc(b.entry.packageName) + "/" + enc(b.entry.className) + "/" + enc(b.entry.methodName) + "/" + enc(b.exceptionType);
                final String passThrough = "?pt=" + b.passThrough;
                return resolveToRoot("/crashdocs/" + entryUri + passThrough);
            } catch (Throwable ignored) {
                return null;
            }
        }
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
