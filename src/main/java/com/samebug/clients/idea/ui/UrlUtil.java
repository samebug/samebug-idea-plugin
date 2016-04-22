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
package com.samebug.clients.idea.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.exceptions.IllegalUriException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by poroszd on 4/15/16.
 */
public class UrlUtil {
    final static Logger LOGGER = Logger.getInstance(UrlUtil.class);

    public static URI getServerRoot() {
        return URI.create(IdeaSamebugPlugin.getInstance().getState().serverRoot);
    }

    public static URL getSearchUrl(int searchId) {
        String uri = "/search/" + searchId;
        try {
            return getServerRoot().resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public static URL getAssetUrl(final String assetUri) {
        try {
            return getServerRoot().resolve("/assets/").resolve(assetUri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri assets/" + assetUri, e);
        }
    }

    public static URL getSourceIconUrl(final String iconId) {
        try {
            return getServerRoot().resolve("/assets/images/sources/").resolve(iconId + ".png").toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri for icon " + iconId, e);
        }
    }

    public static URL getCrashdocUrl(final BreadCrumb b) {
        // TODO currently we can decide between library- and application components
        // only by the component color (0 for application components)
        if (b.component.color == 0) return null;
        else {
            try {
                // TODO handle default package?
                final String entryUri = b.entry.packageName + "/" + b.entry.className + "/" + b.entry.methodName + "/" + b.exceptionType;
                final String passThrough = "?pt=" + b.passThrough;
                return getServerRoot().resolve("/crashdocs/").resolve(java.net.URLEncoder.encode(entryUri, "utf-8") + passThrough).toURL();
            } catch (Throwable e) {
                LOGGER.warn("Unable to resolve uri for breadcrumb", e);
                return null;
            }
        }
    }

    public static URL getUserAvatar() {
        try {
            return new URL(IdeaSamebugPlugin.getInstance().getState().avatarUrl);
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri for user avatar", e);
        }
    }
}
