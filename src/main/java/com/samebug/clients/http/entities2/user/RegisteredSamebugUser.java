package com.samebug.clients.http.entities2.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public final class RegisteredSamebugUser extends SamebugUser {
    @NotNull
    private final Long id;
    @NotNull
    private final String slug;
    @NotNull
    private final String displayName;
    @Nullable
    private final URL avatarUrl;
    @Nullable
    private final Boolean isOnline;

    public RegisteredSamebugUser(@NotNull Long id, @NotNull String slug, @NotNull String displayName, @Nullable URL avatarUrl, @Nullable Boolean isOnline) {
        this.id = id;
        this.slug = slug;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.isOnline = isOnline;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public URL getAvatarUrl() {
        return avatarUrl;
    }

    @Nullable
    public Boolean getOnline() {
        return isOnline;
    }

    @Nullable
    public URL getUrl() {
        try {
            return new URL("https://samebug.io/user/" + id + "/" + slug);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
