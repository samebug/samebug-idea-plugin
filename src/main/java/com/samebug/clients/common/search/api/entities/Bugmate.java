package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public class Bugmate {
    @NotNull
    private int userId;
    @Nullable
    private boolean online;
    @NotNull
    private String displayName;
    @NotNull
    private URL avatarUrl;
    @NotNull
    private Date lastSeen;

    public Bugmate(@NotNull int userId, boolean online, @NotNull String displayName, @NotNull URL avatarUrl, @NotNull Date lastSeen, @NotNull int numberOfSearches) {
        this.userId = userId;
        this.online = online;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.lastSeen = lastSeen;
        this.numberOfSearches = numberOfSearches;
    }

    @NotNull
    public int getUserId() {
        return userId;
    }

    @Nullable
    public boolean isOnline() {
        return online;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public URL getAvatarUrl() {
        return avatarUrl;
    }

    @NotNull
    public Date getLastSeen() {
        return lastSeen;
    }

    @NotNull
    public int getNumberOfSearches() {
        return numberOfSearches;
    }

    @NotNull
    private int numberOfSearches;
}
