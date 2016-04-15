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

import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.exceptions.IllegalUriException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by poroszd on 4/15/16.
 */
public class UrlUtil {
    public static URI getServerRoot() {
        return IdeaSamebugPlugin.getInstance().getState().serverRoot;
    }

    public static URL getSearchUrl(int searchId) {
        String uri = "search/" + searchId;
        try {
            return getServerRoot().resolve(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri " + uri, e);
        }
    }

    public static URL getAssetUrl(final String assetUri) {
        try {
            return getServerRoot().resolve("assets/").resolve(assetUri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalUriException("Unable to resolve uri assets/" + assetUri, e);
        }
    }


}
