package com.samebug.clients.http.entities2.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public abstract class User {
    private URL avatarUrl;

    @NotNull
    public abstract String getDisplayName();

    @Nullable
    public final URL getAvatarUrl() {
        return avatarUrl;
    }

    @Nullable
    public abstract URL getUrl();
}
