package com.samebug.clients.common.api.entities.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SearchInfo {
    @NotNull
    public final Integer id;
    @NotNull
    public final String typeName;
    @Nullable
    public final String message;

    public SearchInfo(@NotNull Integer id, @NotNull String typeName, String message) {
        this.id = id;
        this.typeName = typeName;
        this.message = message;
    }
}
