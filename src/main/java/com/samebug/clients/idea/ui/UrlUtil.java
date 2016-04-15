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
