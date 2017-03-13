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

    public SearchDetails(@NotNull Integer id, @NotNull String typeName, String message, @NotNull Date timestamp) {
        this.id = id;
        this.typeName = typeName;
        this.message = message;
        this.timestamp = timestamp;
    }
}
