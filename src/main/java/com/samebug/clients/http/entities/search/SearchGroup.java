package com.samebug.clients.http.entities.search;

import org.jetbrains.annotations.NotNull;

public abstract class SearchGroup {
    protected String id;

    @NotNull
    public final String getId() {
        return id;
    }
}
