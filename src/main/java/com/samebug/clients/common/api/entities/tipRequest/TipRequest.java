package com.samebug.clients.common.api.entities.tipRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class TipRequest {
    @NotNull
    public final String id;

    public final int searchId;
    @NotNull
    public final String context;
    @NotNull
    public final Date createdAt;
    @Nullable
    public final Date revokedAt;

    public TipRequest(@NotNull String id, int searchId, @NotNull String context, @NotNull Date createdAt, @Nullable Date revokedAt) {
        this.id = id;
        this.searchId = searchId;
        this.context = context;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
    }
}
