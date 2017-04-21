package com.samebug.clients.http.entities2.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public final class RegisteredSamebugUser extends SamebugUser {
    private Long id;
    private String displayName;
    private String slug;
    private Boolean isOnline;

    @NotNull
    public Long getId() {
        return id;
    }

    @Nullable
    public Boolean getOnline() {
        return isOnline;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
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
