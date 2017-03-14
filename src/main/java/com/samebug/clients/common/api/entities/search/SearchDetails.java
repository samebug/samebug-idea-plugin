package com.samebug.clients.common.api.entities.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class SearchDetails {
    @NotNull
    public final Integer id;
    @NotNull
    public final String typeName;
    @Nullable
    public final String message;
    @NotNull
    public final Date timestamp;
    @NotNull
    public final Group group;

    public SearchDetails(@NotNull Integer id, @NotNull String typeName, @Nullable String message, @NotNull Date timestamp, @NotNull Group group) {
        this.id = id;
        this.typeName = typeName;
        this.message = message;
        this.timestamp = timestamp;
        this.group = group;
    }
}
