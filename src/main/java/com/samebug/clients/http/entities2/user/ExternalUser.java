package com.samebug.clients.http.entities2.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class ExternalUser extends User {
    private URL url;
    private String displayName;

    @Nullable
    public URL getUrl() {
        return url;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }
}
