package com.samebug.clients.http.entities2.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class SamebugVisitor extends SamebugUser {
    private String visitorId;
    private Boolean isOnline;

    @NotNull
    public String getVisitorId() {
        return visitorId;
    }

    @Nullable
    public Boolean getOnline() {
        return isOnline;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Unknown visitor";
    }

    @Nullable
    @Override
    public URL getUrl() {
        return null;
    }
}
