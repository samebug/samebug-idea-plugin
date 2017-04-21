package com.samebug.clients.http.entities2.search;

import org.jetbrains.annotations.NotNull;

public abstract class SearchGroup {
    protected String id;

    @NotNull
    public final String getId() {
        return id;
    }
}
